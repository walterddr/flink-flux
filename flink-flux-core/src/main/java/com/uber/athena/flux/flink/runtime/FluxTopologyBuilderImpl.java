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
 */

package com.uber.athena.flux.flink.runtime;

import com.uber.athena.flux.api.topology.FluxTopologyBuilder;
import com.uber.athena.flux.flink.compiler.impl.datastream.FluxCompilerSuite;
import com.uber.athena.flux.model.TopologyDef;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static com.uber.athena.flux.utils.Utils.wrapAsIOException;

/**
 * Default topology builder.
 */
public class FluxTopologyBuilderImpl implements FluxTopologyBuilder {

  private StreamExecutionEnvironment senv;
  private TimeCharacteristic timeCharacteristic;

  FluxTopologyBuilderImpl(Configuration flinkConf) {
    senv = StreamExecutionEnvironment.getExecutionEnvironment();
    // TODO fix time characteristic setting
    timeCharacteristic = TimeCharacteristic.ProcessingTime;
  }

  public static FluxTopologyBuilderImpl createFluxBuilder() {
    FluxTopologyBuilderImpl builder = new FluxTopologyBuilderImpl(new Configuration());
    return builder;
  }

  /**
   * Create {@code FluxTopology} that is used by Flink to execute in runtime.
   *
   * @param topologyDef YAML compiled topology definition
   * @param conf        extra global configuration
   * @return a {@code FluxTopologyImpl} class that contains all required components.
   */
  private FluxTopologyImpl compileTopologyDef(
      StreamExecutionEnvironment senv,
      TopologyDef topologyDef,
      Map<String, Object> conf) {
    Configuration config = generateFlinkConfiguration(conf);

    // create compilation suite
    FluxCompilerSuite compilerSuite = new FluxCompilerSuite(topologyDef, config, senv);

    // Compile the topology
    return compilerSuite.compile();
  }

  private static Configuration generateFlinkConfiguration(Map<String, Object> conf) {
    // parse configurations
    // TODO: make this more flexible
    Properties props = new Properties();
    if (conf != null) {
      props.putAll(conf);
    }
    Configuration config = new Configuration();
    config.addAllToProperties(props);
    return config;
  }

  /**
   * Compile into a {@code FluxTopology}.
   *
   * @param topologyDef topology def
   * @param config global config
   * @return flux topology
   * @throws IOException when compilation fails
   */
  @Override
  public FluxTopologyImpl createTopology(
      TopologyDef topologyDef,
      Map<String, Object> config) throws IOException {

    // set time characteristic first
    senv.setStreamTimeCharacteristic(timeCharacteristic);

    try {
      return compileTopologyDef(senv, topologyDef, config);
    } catch (Exception e) {
      throw wrapAsIOException(e);
    }
  }
}

