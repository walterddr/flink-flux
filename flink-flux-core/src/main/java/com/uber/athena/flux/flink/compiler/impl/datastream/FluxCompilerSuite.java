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

package com.uber.athena.flux.flink.compiler.impl.datastream;

import com.uber.athena.flux.flink.runtime.FluxTopologyImpl;
import com.uber.athena.flux.model.TopologyDef;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Preconditions;

/**
 * Compilation framework for the flux topology.
 *
 * <p>Based on the topology definition type, and the supported compiler,
 * this compiler suite will find the appropriate compilation framework
 * to construct the Flink topology job graph.
 */
public class FluxCompilerSuite {

  private final TopologyDef topologyDef;
  private final Configuration config;
  private final StreamExecutionEnvironment streamExecutionEnvironment;
  private final FluxContext fluxContext;

  private CompilationGraph compilationGraph;

  public FluxCompilerSuite(
      TopologyDef topologyDef,
      Configuration config,
      StreamExecutionEnvironment streamExecutionEnvironment) {
    this.streamExecutionEnvironment = streamExecutionEnvironment;
    this.topologyDef = topologyDef;
    this.config = new Configuration(config);
    this.fluxContext = new FluxContext(topologyDef, config);
    this.compilationGraph = new CompilationGraph(
        this.streamExecutionEnvironment,
        this.fluxContext);
  }

  /**
   * compile topology definition to {@code FluxTopology}.
   *
   * <p>The compilation should invoke the compilation framework based on
   * constructed settings.
   *
   * @return a flux topology, different compilation suits might return different implementations.
   */
  public FluxTopologyImpl compile() {
    Preconditions.checkNotNull(topologyDef, "topology cannot be null!");
    Preconditions.checkNotNull(streamExecutionEnvironment, "execution environment cannot be null!");
    return this.compileInternal();
  }

  private FluxTopologyImpl compileInternal() {
    return this.compilationGraph.compile();
  }
}
