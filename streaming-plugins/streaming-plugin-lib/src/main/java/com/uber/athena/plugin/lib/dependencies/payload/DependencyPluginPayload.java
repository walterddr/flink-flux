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

package com.uber.athena.plugin.lib.dependencies.payload;

import com.uber.athena.plugin.api.PluginPayload;

import java.util.Properties;

import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverBuilder.ALLOW_MISSING_ARTIFACTS;
import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverBuilder.ARTIFACTS;
import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverBuilder.ARTIFACT_REPOSITORIES;
import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverBuilder.MAVEN_LOCAL_REPO_DIR;
import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverBuilder.PROXY_PASSWORD;
import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverBuilder.PROXY_URL;
import static com.uber.athena.plugin.lib.dependencies.resolver.DependencyResolverBuilder.PROXY_USERNAME;

/**
 * {@link PluginPayload} for Flink DSL construct.
 */
public class DependencyPluginPayload implements PluginPayload<DependencyPluginPayload> {

  private Properties properties;

  public DependencyPluginPayload(Properties payloadProperties) {
    this.properties = payloadProperties;
  }

  public Properties getProperties() {
    return properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  /**
   * Builder pattern for constructing the plugin payload efficiently.
   */
  public static class Builder {
    private Properties props;

    public Builder() {
      props = new Properties();
    }

    public Builder setArtifactList(String artifactList) {
      this.props.put(ARTIFACTS, artifactList);
      return this;
    }

    public Builder  setArtifactRepo(String artifactRepo) {
      this.props.put(ARTIFACT_REPOSITORIES, artifactRepo);
      return this;
    }

    public Builder  setMavenLocalDir(String mavenLocalDir) {
      this.props.put(MAVEN_LOCAL_REPO_DIR, mavenLocalDir);
      return this;
    }

    public Builder  setProxyUrl(String proxyUrl) {
      this.props.put(PROXY_URL, proxyUrl);
      return this;
    }

    public Builder  setProxyUsername(String proxyUsername) {
      this.props.put(PROXY_USERNAME, proxyUsername);
      return this;
    }

    public Builder  setProxyPassword(String proxyPassword) {
      this.props.put(PROXY_PASSWORD, proxyPassword);
      return this;
    }

    public Builder  setAllowMissingArtifacts(boolean allowMissingArtifacts) {
      this.props.put(ALLOW_MISSING_ARTIFACTS, allowMissingArtifacts);
      return this;
    }

    public DependencyPluginPayload build() {
      return new DependencyPluginPayload(props);
    }
  }
}
