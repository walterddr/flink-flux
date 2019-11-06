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

package com.uber.athena.plugin.lib.dependencies;

import com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverUtil;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for supporting dependency plugin execution.
 */
public final class DependencyPluginUtils {
  private DependencyPluginUtils() {

  }

  // --------------------------------------------------------------------------
  // Parsing arguments and printing system messages.
  // --------------------------------------------------------------------------

  public static void printMissingArtifactsToSysErr(Iterable<ArtifactResult> missingArtifacts) {
    for (ArtifactResult artifactResult : missingArtifacts) {
      System.err.println("ArtifactResult : " + artifactResult
          + " / Errors : " + artifactResult.getExceptions());
    }
  }

  public static List<Dependency> parseArtifactArgs(String artifactArgs) {
    List<String> artifacts = Arrays.asList(artifactArgs.split(","));
    List<Dependency> dependencies = new ArrayList<>(artifacts.size());
    for (String artifactOpt : artifacts) {
      if (artifactOpt.trim().isEmpty()) {
        continue;
      }
      dependencies.add(DependencyResolverUtil.parseDependency(artifactOpt));
    }

    return dependencies;
  }

  public static List<RemoteRepository> parseRemoteRepositoryArgs(String remoteRepositoryArg) {
    List<String> repositories = Arrays.asList(remoteRepositoryArg.split(","));
    List<RemoteRepository> remoteRepositories = new ArrayList<>(repositories.size());
    for (String repositoryOpt : repositories) {
      if (repositoryOpt.trim().isEmpty()) {
        continue;
      }

      remoteRepositories.add(DependencyResolverUtil.parseRemoteRepository(repositoryOpt));
    }
    return remoteRepositories;
  }

  public static Proxy parseProxyArg(
      String proxyUrl,
      String proxyUsername,
      String proxyPassword) throws MalformedURLException {
    URL url = new URL(proxyUrl);
    if (StringUtils.isNotEmpty(proxyUsername) && StringUtils.isNotEmpty(proxyPassword)) {
      AuthenticationBuilder authBuilder = new AuthenticationBuilder();
      authBuilder.addUsername(proxyUsername).addPassword(proxyPassword);
      return new Proxy(url.getProtocol(), url.getHost(), url.getPort(), authBuilder.build());
    } else {
      return new Proxy(url.getProtocol(), url.getHost(), url.getPort());
    }
  }

  public static Map<String, String> getArtifactToPathsMapping(
      List<ArtifactResult> artifactResults) {
    Map<String, String> artifactToPath = new LinkedHashMap<>();
    for (ArtifactResult artifactResult : artifactResults) {
      Artifact artifact = artifactResult.getArtifact();
      artifactToPath.put(
          DependencyResolverUtil.artifactToString(artifact),
          artifact.getFile().getAbsolutePath());
    }
    return artifactToPath;
  }

  // --------------------------------------------------------------------------
  // Maven repository utilities
  // --------------------------------------------------------------------------

  public static String getOrDefaultLocalMavenRepositoryPath(
      String customLocalMavenPath,
      String defaultPath) {
    if (customLocalMavenPath != null) {
      Path customPath = new File(customLocalMavenPath).toPath();
      if (Files.exists(customPath) && !Files.isDirectory(customPath)) {
        throw new IllegalArgumentException(
            "Custom local maven repository path exist and is not a directory!");
      }
      return customLocalMavenPath;
    }

    String localMavenRepoPathStr = getLocalMavenRepositoryPath();
    if (StringUtils.isNotEmpty(localMavenRepoPathStr)) {
      Path localMavenRepoPath = new File(localMavenRepoPathStr).toPath();
      if (Files.exists(localMavenRepoPath) && Files.isDirectory(localMavenRepoPath)) {
        return localMavenRepoPathStr;
      }
    }

    return defaultPath;
  }

  public static String getLocalMavenRepositoryPath() {
    String userHome = System.getProperty("user.home");
    if (StringUtils.isNotEmpty(userHome)) {
      return userHome + File.separator + ".m2" + File.separator + "repository";
    }

    return null;
  }
}
