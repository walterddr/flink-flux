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

package com.uber.athena.flux.flink.compiler.impl.test;

import com.uber.athena.flux.api.topology.FluxTopologyBuilder;
import com.uber.athena.flux.flink.compiler.context.CompilerContext;
import com.uber.athena.flux.flink.compiler.context.CompilerGraph;
import com.uber.athena.flux.flink.compiler.impl.test.factory.BasicCompilerFactory;
import com.uber.athena.flux.flink.compiler.runtime.FlinkFluxTopology;
import com.uber.athena.flux.model.TopologyDef;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Preconditions;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class BasicFluxTopologyBuilderImpl implements FluxTopologyBuilder {

  private final TopologyDef topologyDef;
  private final StreamExecutionEnvironment streamExecutionEnvironment;
  private final CompilerContext compilerContext;
  private CompilerGraph compilerGraph;

  public BasicFluxTopologyBuilderImpl(
      TopologyDef topologyDef,
      StreamExecutionEnvironment streamExecutionEnvironment) {
    this(topologyDef, streamExecutionEnvironment,
        generateFlinkConfiguration(Collections.emptyMap()));
  }

  public BasicFluxTopologyBuilderImpl(
      TopologyDef topologyDef,
      StreamExecutionEnvironment streamExecutionEnvironment,
      Configuration config) {
    this.streamExecutionEnvironment = streamExecutionEnvironment;
    this.topologyDef = topologyDef;
    this.compilerContext = new CompilerContext(topologyDef, config);
    this.compilerGraph = new BasicCompilerGraphImpl(
        this.streamExecutionEnvironment,
        this.compilerContext,
        BasicCompilerFactory.class) {
    };
  }

  /**
   * compile topology definition to {@code FluxTopology}.
   *
   * <p>The compilation should invoke the compilation framework based on
   * constructed settings.
   *
   * @return a flux topology, different compilation suits might return different implementations.
   */
  @Override
  public FlinkFluxTopology createTopology(TopologyDef topologyDef, Map<String, Object> config) throws IOException {
    Preconditions.checkNotNull(topologyDef, "topology cannot be null!");
    Preconditions.checkNotNull(streamExecutionEnvironment, "execution environment cannot be null!");
    return this.compileInternal();
  }

  private FlinkFluxTopology compileInternal() {
    return this.compilerGraph.compile();
  }

  private static Configuration generateFlinkConfiguration(Map<String, Object> conf) {
    // parse configurations
    Properties props = new Properties();
    if (conf != null) {
      props.putAll(conf);
    }
    Configuration config = new Configuration();
    config.addAllToProperties(props);
    return config;
  }
}
