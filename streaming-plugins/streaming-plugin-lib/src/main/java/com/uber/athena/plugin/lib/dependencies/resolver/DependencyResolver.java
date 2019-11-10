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

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Resolver class of dependencies.
 */
public class DependencyResolver {
  private RepositorySystem system = Booter.newRepositorySystem();

  private String localRepoPath;
  private List<RemoteRepository> remoteRepositories;
  private List<Dependency> dependencies;
  private boolean allowMissingArtifacts;

  private Proxy proxy = null;
  private RepositorySystemSession session = null;

  DependencyResolver(String localRepoPath) {
    this(localRepoPath, Collections.emptyList());
  }

  DependencyResolver(String localRepoPath, List<RemoteRepository> repositories) {
    this.localRepoPath = localRepoPath;
    this.remoteRepositories = new ArrayList<>();
    this.remoteRepositories.add(Booter.newCentralRepository());
    this.remoteRepositories.addAll(repositories);
  }

  /**
   * Resolve dependencies and return downloaded information of artifacts.
   *
   * @return downloaded information of artifacts
   * @throws DependencyResolutionException If the dependency tree could not be
   *                                       built or any dependency artifact could not be resolved.
   * @throws ArtifactResolutionException   If the artifact could not be resolved.
   */
  public List<ArtifactResult> resolve() throws
      DependencyResolutionException, ArtifactResolutionException {
    if (session == null) {
      throw new IllegalStateException("Unable to resolve dependency, resolver not initialized!");
    }

    // Acquire dependencies.
    if (dependencies.size() == 0) {
      return Collections.emptyList();
    }

    CollectRequest collectRequest = new CollectRequest();
    collectRequest.setRoot(dependencies.get(0));
    for (int idx = 1; idx < dependencies.size(); idx++) {
      collectRequest.addDependency(dependencies.get(idx));
    }

    for (RemoteRepository repository : remoteRepositories) {
      collectRequest.addRepository(repository);
    }
    DependencyFilter classpathFilter = DependencyFilterUtils
        .classpathFilter(JavaScopes.COMPILE, JavaScopes.RUNTIME);
    DependencyRequest dependencyRequest =
        new DependencyRequest(collectRequest, classpathFilter);
    List<ArtifactResult> artifactResults = system
        .resolveDependencies(session, dependencyRequest)
        .getArtifactResults();

    if (!allowMissingArtifacts) {
      List<ArtifactResult> missingArtifacts = artifactResults
          .stream()
          .filter(ArtifactResult::isMissing)
          .collect(Collectors.toList());
      if (missingArtifacts.iterator().hasNext()) {
        throw new ArtifactResolutionException(missingArtifacts);
      }
    }
    return artifactResults;
  }

  public String getLocalRepoPath() {
    return localRepoPath;
  }

  /**
   * Initialize the dependency resolver.
   *
   * @throws Exception when initialization fails.
   */
  void initialize() throws Exception {
    // Initialize resolver system.
    localRepoPath = DependencyResolverUtil.handleRelativePath(
        localRepoPath, true);
    session = Booter.newRepositorySystemSession(system, localRepoPath);
  }

  /**
   * Setter of proxy if needed.
   *
   * @param proxyParam proxy object
   */
  void setProxy(Proxy proxyParam) {
    this.proxy = proxyParam;
    List<RemoteRepository> appliedRepositories =
        new ArrayList<>(remoteRepositories.size());
    for (RemoteRepository repository : remoteRepositories) {
      appliedRepositories.add(
          new RemoteRepository.Builder(repository).setProxy(proxy).build());
    }
    this.remoteRepositories = appliedRepositories;
  }

  public void setDependencies(List<Dependency> dependencies) {
    this.dependencies = dependencies;
  }

  public void isAllowMissingArtifacts(boolean allowMissingArtifacts) {
    this.allowMissingArtifacts = allowMissingArtifacts;
  }
}
