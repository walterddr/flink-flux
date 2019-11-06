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
import com.uber.athena.plugin.lib.utils.SerializationUtils;

import java.util.Properties;

/**
 * {@link PluginPayload} for Flink DSL construct.
 */
public class DependencyPluginPayload implements PluginPayload<DependencyPluginPayload> {
  public static final String DEFAULT_FALLBACK_MAVEN_LOCAL_REPOSITORY_DIRECTORY = "local-repo";
  public static final String OPTION_ARTIFACTS_LONG = "artifacts";
  public static final String OPTION_ARTIFACT_REPOSITORIES_LONG = "artifactRepositories";
  public static final String OPTION_MAVEN_LOCAL_REPO_DIR_LONG = "mavenLocalRepositoryDirectory";
  public static final String OPTION_ALLOW_MISSING_ARTIFACTS = "allowMissingArtifacts";
  public static final String OPTION_PROXY_URL_LONG = "proxyUrl";
  public static final String OPTION_PROXY_USERNAME_LONG = "proxyUsername";
  public static final String OPTION_PROXY_PASSWORD_LONG = "proxyPassword";

  private Properties properties;

  public DependencyPluginPayload(Properties payloadProperties) {
    this.properties = payloadProperties;
  }

  @Override
  public byte[] serialize() throws Exception {
    return SerializationUtils.serializerJavaObj(this);
  }

  @Override
  public DependencyPluginPayload deserialize(byte[] serializedObj) throws Exception {
    return SerializationUtils.javaDeserialize(serializedObj);
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
      this.props.put(OPTION_ARTIFACTS_LONG, artifactList);
      return this;
    }

    public Builder setArtifactRepo(String artifactRepo) {
      this.props.put(OPTION_ARTIFACT_REPOSITORIES_LONG, artifactRepo);
      return this;
    }

    public Builder setMavenLocalDir(String mavenLocalDir) {
      this.props.put(OPTION_MAVEN_LOCAL_REPO_DIR_LONG, mavenLocalDir);
      return this;
    }

    public Builder setProxyUrl(String proxyUrl) {
      this.props.put(OPTION_PROXY_URL_LONG, proxyUrl);
      return this;
    }

    public Builder setProxyUsername(String proxyUsername) {
      this.props.put(OPTION_PROXY_USERNAME_LONG, proxyUsername);
      return this;
    }

    public Builder setProxyPassword(String proxyPassword) {
      this.props.put(OPTION_PROXY_PASSWORD_LONG, proxyPassword);
      return this;
    }

    public Builder setAllowMissingArtifacts(boolean allowMissingArtifacts) {
      this.props.put(OPTION_ALLOW_MISSING_ARTIFACTS, allowMissingArtifacts);
      return this;
    }

    public DependencyPluginPayload build() {
      return new DependencyPluginPayload(props);
    }
  }
}
