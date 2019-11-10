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

package com.uber.athena.plugin.lib.dependencies.resolver;

import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverUtil.getOrDefaultLocalMavenRepositoryPath;
import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverUtil.parseArtifactArgs;
import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverUtil.parseProxyArg;
import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverUtil.parseRemoteRepositoryArgs;

/**
 * Resolver configuration utility to construct a {@link DependencyResolver}.
 */
public class DependencyResolverBuilder {
  public static final String DEFAULT_HOME_PATH_ENV_PARAM_KEY = "DEPENDENCY_RESOLVER_HOME_PATH";
  public static final String DEFAULT_FALLBACK_MAVEN_LOCAL_REPOSITORY_DIRECTORY = "local-repo";

  public static final String ARTIFACTS = "artifacts";
  public static final String ARTIFACT_REPOSITORIES = "artifactRepositories";
  public static final String MAVEN_LOCAL_REPO_DIR = "mavenLocalRepositoryDirectory";
  public static final String ALLOW_MISSING_ARTIFACTS = "allowMissingArtifacts";
  public static final String PROXY_URL = "proxyUrl";
  public static final String PROXY_USERNAME = "proxyUsername";
  public static final String PROXY_PASSWORD = "proxyPassword";

  private Properties props;

  public DependencyResolverBuilder(Properties properties) {
    props = properties;
  }

  public DependencyResolver build() {
    try {
      // 1. resolve repositories
      List<RemoteRepository> repositories;
      if (props.containsKey(ARTIFACT_REPOSITORIES)) {
        String remoteRepository = props.getProperty(ARTIFACT_REPOSITORIES);
        repositories = parseRemoteRepositoryArgs(remoteRepository);
      } else {
        repositories = Collections.emptyList();
      }

      // 2. setup maven local repository path for downloading dependencies.
      String localMavenRepoPath =
          getOrDefaultLocalMavenRepositoryPath(
              props.getProperty(MAVEN_LOCAL_REPO_DIR),
              DEFAULT_FALLBACK_MAVEN_LOCAL_REPOSITORY_DIRECTORY);

      Files.createDirectories(new File(localMavenRepoPath).toPath());

      DependencyResolver resolver = new DependencyResolver(localMavenRepoPath, repositories);

      // 3. resolve Artifacts
      String artifacts = props.getProperty(ARTIFACTS);
      List<Dependency> dependencies = parseArtifactArgs(artifacts);
      resolver.setDependencies(dependencies);

      // 4. setup proxy
      if (props.containsKey(PROXY_URL)) {
        String proxyUrl = props.getProperty(PROXY_URL);
        String proxyUsername = props.getProperty(PROXY_USERNAME);
        String proxyPassword = props.getProperty(PROXY_PASSWORD);
        resolver.setProxy(parseProxyArg(proxyUrl, proxyUsername, proxyPassword));
      }

      // 5. setup additional configuration rules.
      if (!props.containsKey(ALLOW_MISSING_ARTIFACTS)) {
        resolver.isAllowMissingArtifacts(Boolean.valueOf(
            (String) props.get(ALLOW_MISSING_ARTIFACTS)));
      }

      // 6. initialize resolver
      resolver.initialize();

      return resolver;
    } catch (Exception e) {
      throw new UnsupportedOperationException("Error constructing dependency resolver!", e);
    }
  }

}
