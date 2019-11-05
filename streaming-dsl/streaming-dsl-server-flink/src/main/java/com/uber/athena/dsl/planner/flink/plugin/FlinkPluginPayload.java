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

package com.uber.athena.dsl.planner.flink.plugin;

import com.uber.athena.plugin.api.PluginPayload;
import org.apache.flink.configuration.Configuration;

import java.util.Map;
import java.util.Properties;

import static com.uber.athena.dsl.planner.flink.plugin.SerializationUtils.javaDeserialize;
import static com.uber.athena.dsl.planner.flink.plugin.SerializationUtils.serializerJavaObj;

/**
 * {@link PluginPayload} for Flink DSL construct.
 */
public class FlinkPluginPayload implements PluginPayload<FlinkPluginPayload> {

  private String inputFile;
  private PluginRuleSetType ruleSetType;
  private Configuration flinkConf;
  private Map<String, Object> config;
  private Properties properties;

  public FlinkPluginPayload(
      String inputFile,
      PluginRuleSetType ruleSetType,
      Configuration flinkConf,
      Map<String, Object> config,
      Properties properties
  ) {
    this.inputFile = inputFile;
    this.ruleSetType = ruleSetType;
    this.flinkConf = flinkConf;
    this.config = config;
    this.properties = properties;
  }

  @Override
  public byte[] serialize() throws Exception {
    return serializerJavaObj(this);
  }

  @Override
  public FlinkPluginPayload deserialize(byte[] serializedObj) throws Exception {
    return javaDeserialize(serializedObj);
  }

  public Configuration getFlinkConf() {
    return flinkConf;
  }

  public Map<String, Object> getConfig() {
    return config;
  }

  public Properties getProperties() {
    return properties;
  }

  public PluginRuleSetType getRuleSetType() {
    return ruleSetType;
  }

  public String getInputFile() {
    return inputFile;
  }

  /**
   * Supported standard plugin rule set enums.
   */
  public enum PluginRuleSetType {
    DATASTREAM,
    TABLE
  }
}
