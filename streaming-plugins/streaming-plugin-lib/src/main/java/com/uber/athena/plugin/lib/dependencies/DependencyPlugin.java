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
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.uber.athena.plugin.lib.dependencies.DependencyPluginUtils.getArtifactToPathsMapping;
import static com.uber.athena.plugin.lib.dependencies.DependencyPluginUtils.getOrDefaultLocalMavenRepositoryPath;
import static com.uber.athena.plugin.lib.dependencies.DependencyPluginUtils.parseArtifactArgs;
import static com.uber.athena.plugin.lib.dependencies.DependencyPluginUtils.parseProxyArg;
import static com.uber.athena.plugin.lib.dependencies.DependencyPluginUtils.parseRemoteRepositoryArgs;
import static com.uber.athena.plugin.lib.dependencies.DependencyPluginUtils.printMissingArtifactsToSysErr;
import static com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginPayload.DEFAULT_FALLBACK_MAVEN_LOCAL_REPOSITORY_DIRECTORY;
import static com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginPayload.OPTION_ALLOW_MISSING_ARTIFACTS;
import static com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginPayload.OPTION_ARTIFACTS_LONG;
import static com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginPayload.OPTION_ARTIFACT_REPOSITORIES_LONG;
import static com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginPayload.OPTION_MAVEN_LOCAL_REPO_DIR_LONG;
import static com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginPayload.OPTION_PROXY_PASSWORD_LONG;
import static com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginPayload.OPTION_PROXY_URL_LONG;
import static com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginPayload.OPTION_PROXY_USERNAME_LONG;

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

    // 1. resolve list of artifacts/dependencies.
    payloadProperties = payload.getProperties();
    if (!payloadProperties.containsKey(OPTION_ARTIFACTS_LONG)) {
      throw new IllegalArgumentException("artifacts must be presented to "
          + "execute dependency plugin.");
    }
  }

  @Override
  public PluginResult run() throws Exception {
    String artifactsArg = payloadProperties.getProperty(OPTION_ARTIFACTS_LONG);
    // System.err.println("DependencyResolver input - artifacts: " + artifactsArg);
    List<Dependency> dependencies = parseArtifactArgs(artifactsArg);

    List<RemoteRepository> repositories;
    // 2. OPTIONAL resolve artifact repositories.
    if (payloadProperties.containsKey(OPTION_ARTIFACT_REPOSITORIES_LONG)) {
      String remoteRepositoryArg = payloadProperties.getProperty(OPTION_ARTIFACT_REPOSITORIES_LONG);
      // System.err.println("DependencyResolver input - repositories: " + remoteRepositoryArg);
      repositories = parseRemoteRepositoryArgs(remoteRepositoryArg);
    } else {
      repositories = Collections.emptyList();
    }

    try {
      // 3. setup maven local repository path for downloading dependencies.
      String localMavenRepoPath = getOrDefaultLocalMavenRepositoryPath(
          payloadProperties.getProperty(OPTION_MAVEN_LOCAL_REPO_DIR_LONG),
          DEFAULT_FALLBACK_MAVEN_LOCAL_REPOSITORY_DIRECTORY);

      Files.createDirectories(new File(localMavenRepoPath).toPath());

      // 4. initialize and launch the resolver.
      DependencyResolver resolver = new DependencyResolver(localMavenRepoPath, repositories);

      if (payloadProperties.containsKey(OPTION_PROXY_URL_LONG)) {
        String proxyUrl = payloadProperties.getProperty(OPTION_PROXY_URL_LONG);
        String proxyUsername = payloadProperties.getProperty(OPTION_PROXY_USERNAME_LONG);
        String proxyPassword = payloadProperties.getProperty(OPTION_PROXY_PASSWORD_LONG);

        resolver.setProxy(parseProxyArg(proxyUrl, proxyUsername, proxyPassword));
      }

      List<ArtifactResult> artifactResults = resolver.resolve(dependencies);

      // 5. check if there's missing artifacts unable to be resolved.
      if (!payloadProperties.containsKey(OPTION_ALLOW_MISSING_ARTIFACTS)) {
        Iterable<ArtifactResult> missingArtifacts = artifactResults
            .stream()
            .filter(ArtifactResult::isMissing)
            .collect(Collectors.toList());
        if (missingArtifacts.iterator().hasNext()) {
          printMissingArtifactsToSysErr(missingArtifacts);
          throw new UnsupportedOperationException("Some artifacts are not resolved! "
              + StreamSupport
              .stream(missingArtifacts.spliterator(), false)
              .map(a -> String.join(":",
                  a.getArtifact().getGroupId(), a.getArtifact().getArtifactId()))
              .reduce("", (s1, s2) -> String.join("\n", s1, s2))
          );
        }
      }

      Map<String, String> artifactToPathsMapping = getArtifactToPathsMapping(artifactResults);
      return new DependencyPluginResult(artifactToPathsMapping);
    } catch (Exception e) {
      throw new UnsupportedOperationException("Error occurred during dependency resolution!", e);
    }

  }
}
