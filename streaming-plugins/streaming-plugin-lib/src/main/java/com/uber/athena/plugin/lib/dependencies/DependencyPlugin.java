/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.uber.athena.plugin.lib.dependencies;

import com.uber.athena.plugin.api.Plugin;
import com.uber.athena.plugin.api.PluginPayload;
import com.uber.athena.plugin.api.PluginResult;
import com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginPayload;
import com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginResult;
import com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolver;
import com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverBuilder;
import org.eclipse.aether.resolution.ArtifactResult;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverBuilder.ARTIFACTS;
import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverUtil.getArtifactToPathsMapping;

/**
 * The {@link Plugin} for executing dependency resolver.
 */
public class DependencyPlugin implements Plugin {

  private Properties payloadProperties;
  private DependencyPluginPayload payload;

  @Override
  public void instantiate(PluginPayload pluginPayload) {
    if (!(pluginPayload instanceof DependencyPluginPayload)) {
      throw new IllegalArgumentException("Cannot instantiate Dependency plugin "
          + "with given payload: " + pluginPayload.getClass());
    }
    payload = (DependencyPluginPayload) pluginPayload;

    // Resolve list of artifacts/dependencies.
    payloadProperties = payload.getProperties();
    if (!payloadProperties.containsKey(ARTIFACTS)) {
      throw new IllegalArgumentException("artifacts must be presented to "
          + "execute dependency plugin.");
    }
  }

  @Override
  public PluginResult run() throws Exception {
    DependencyResolver resolver = new DependencyResolverBuilder(payloadProperties).build();
    List<ArtifactResult> artifactResults = resolver.resolve();
    Map<String, String> artifactToPathsMapping = getArtifactToPathsMapping(artifactResults);
    return new DependencyPluginResult(artifactToPathsMapping);
  }
}
