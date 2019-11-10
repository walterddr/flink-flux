/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.uber.athena.plugin.lib.dsl;

import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.plugin.api.PluginResult;
import com.uber.athena.plugin.base.ExceptionPluginResult;
import com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolver;
import com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverBuilder;
import com.uber.athena.plugin.lib.dsl.payload.FlinkPluginDependencyResult;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.resolution.ArtifactResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverUtil.artifactToString;

/**
 * Plugin returns the DSL constructed job graph as {@link PluginResult}.
 */
public class FlinkDslDependencyPlugin extends FlinkPlugin {

  @Override
  public PluginResult run() throws Exception {
    String filename = payload.getInputFile();
    try (InputStream file = new FileInputStream(filename)) {
      Topology topology = planner.parse(file);
      topology = planner.validate(topology);

      List<String> dependencies = topology.getDependencies();
      if (dependencies == null || dependencies.size() == 0) {
        return new FlinkPluginDependencyResult("");
      }

      Properties props = new Properties();
      // put custom properties via the config map.
      props.putAll(topology.getConfig());
      props.put(DependencyResolverBuilder.ARTIFACTS, String.join(",", dependencies));
      DependencyResolver resolver = new DependencyResolverBuilder(props).build();
      List<ArtifactResult> result = resolver.resolve();
      return new FlinkPluginDependencyResult(
          resolver.getLocalRepoPath(),
          flattenArtifactPaths(result, resolver.getLocalRepoPath())
      );
    } catch (Exception e) {
      return new ExceptionPluginResult(
          new IOException("Unable to resolve dependencies", e));
    }
  }

  private static List<String> flattenArtifactPaths(
      List<ArtifactResult> results,
      String localPath) throws IOException {
    List<String> artifactList = new ArrayList<>();
    for (ArtifactResult result : results) {
      Artifact artifact = result.getArtifact();
      File sourceFile = artifact.getFile();
      File destFile = new File(localPath + "/" + sourceFile.getName());
      if (destFile.exists()) {
        boolean isDeleted = destFile.delete();
        if (!isDeleted) {
          throw new IOException("Unable to delete original file before copying:" + destFile);
        }
      }
      Files.move(sourceFile.toPath(), destFile.toPath());
      artifactList.add(artifactToString(artifact));
    }
    return artifactList;
  }
}
