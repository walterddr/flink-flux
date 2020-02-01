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

package com.uber.athena.plugin.lib.dependencies.resolver;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;
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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverBuilder.DEFAULT_HOME_PATH_ENV_PARAM_KEY;

/**
 * Maven dependency resolver utility utilizing.
 */
public final class DependencyResolverUtil {
  private static final int GROUP_IDX = 0;
  private static final int ARTIFACT_IDX = 1;
  private static final int CLASSIFIER_IDX = 2;
  private static final int EXTENSION_IDX = 3;
  private static final int BUFFER_CAPACITY = 128;

  private DependencyResolverUtil() {
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

  // --------------------------------------------------------------------------
  // Basic utilities
  // --------------------------------------------------------------------------

  /**
   * Parses dependency parameter and build {@link Dependency} object.
   *
   * @param dependency string representation of dependency parameter
   * @return Dependency object
   */
  public static Dependency parseDependency(String dependency) {
    List<String> dependencyAndExclusions = Arrays.asList(dependency.split("\\^"));
    Collection<Exclusion> exclusions = new ArrayList<>();
    for (int idx = 1; idx < dependencyAndExclusions.size(); idx++) {
      exclusions.add(DependencyResolverUtil.createExclusion(dependencyAndExclusions.get(idx)));
    }

    Artifact artifact = new DefaultArtifact(dependencyAndExclusions.get(0));
    return new Dependency(artifact, "compile", false, exclusions);
  }

  /**
   * Parses exclusion parameter and build {@link Exclusion} object.
   *
   * @param exclusionString string representation of exclusion parameter
   * @return Exclusion object
   */
  public static Exclusion createExclusion(String exclusionString) {
    String[] parts = exclusionString.split(":");

    String groupId = null;
    String artifactId = "*";
    String classifier = "*";
    String extension = "*";

    for (int i = 0; i < parts.length; i++) {
      switch (i) {
        case GROUP_IDX:
          groupId = parts[i];
          break;
        case ARTIFACT_IDX:
          artifactId = parts[i];
          break;
        case CLASSIFIER_IDX:
          classifier = parts[i];
          break;
        case EXTENSION_IDX:
          extension = parts[i];
          break;
        default: break;
      }
    }

    if (groupId != null) {
      return new Exclusion(groupId, artifactId, classifier, extension);
    } else {
      return null;
    }
  }

  /**
   * Convert {@link Artifact} object to String for printing.
   *
   * @param artifact Artifact object
   * @return String representation of Artifact
   */
  public static String artifactToString(Artifact artifact) {
    StringBuilder buffer = new StringBuilder(BUFFER_CAPACITY);
    buffer.append(artifact.getGroupId());
    buffer.append(':').append(artifact.getArtifactId());
    buffer.append(':').append(artifact.getExtension());
    if (artifact.getClassifier().length() > 0) {
      buffer.append(':').append(artifact.getClassifier());
    }
    buffer.append(':').append(artifact.getVersion());
    return buffer.toString();
  }

  /**
   * Parses remote repository parameter and build {@link RemoteRepository} object.
   *
   * @param repository string representation of remote repository parameter
   * @return RemoteRepository object
   */
  public static RemoteRepository parseRemoteRepository(String repository) {
    String[] parts = repository.split("\\^");
    if (parts.length < 2) {
      throw new IllegalArgumentException("Bad remote repository form: " + repository);
    }

    return new RemoteRepository.Builder(parts[0], "default", parts[1]).build();
  }

  /**
   * Handle relative path for local repository.
   *
   * @param localRepoPath the local repository paths
   * @return the resolved absolute local path for the repository.
   */
  public static String handleRelativePath(
      String localRepoPath,
      boolean isFallbackToCurrent) {
    File repoDir = new File(localRepoPath);
    if (!repoDir.isAbsolute()) {
      // find homedir
      String home = System.getProperty(DEFAULT_HOME_PATH_ENV_PARAM_KEY);
      if (home == null) {
        if (isFallbackToCurrent) {
          home = ".";
        } else {
          throw new IllegalArgumentException("Cannot resolve relative path, "
              + "no default home path found in env by key: " + DEFAULT_HOME_PATH_ENV_PARAM_KEY);
        }
      }
      localRepoPath = home + "/" + localRepoPath;
    }
    return localRepoPath;
  }
}

