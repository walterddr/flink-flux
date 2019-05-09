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

package com.uber.athena.flux.flink.compiler;

import com.uber.athena.flux.api.topology.FluxTopology;
import com.uber.athena.flux.flink.runtime.FluxTopologyImpl;
import com.uber.athena.flux.model.EdgeDef;
import com.uber.athena.flux.model.OperatorDef;
import com.uber.athena.flux.model.SinkDef;
import com.uber.athena.flux.model.SourceDef;
import com.uber.athena.flux.model.StreamDef;
import com.uber.athena.flux.model.TopologyDef;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Object holder for compilation procedure.
 */
public class CompilationGraph {
  private final FluxContext fluxContext;
  private final StreamExecutionEnvironment senv;
  private Queue<CompilationVertex> compilationQueue = new PriorityQueue<>();

  public CompilationGraph(StreamExecutionEnvironment senv, FluxContext fluxContext) {
    this.senv = senv;
    this.fluxContext = fluxContext;
  }

  /**
   * Compile current graph into a {@link FluxTopology}.
   *
   * @return the topology
   */
  public FluxTopologyImpl compile() {
    constructCompilationGraph(fluxContext);
    compileVertexQueue(senv, fluxContext);
    JobGraph jobGraph = senv.getStreamGraph().getJobGraph();
    FluxTopologyImpl fluxTopology = new FluxTopologyImpl();
    fluxTopology.setJobGraph(jobGraph);
    return fluxTopology;
  }

  private void constructCompilationGraph(FluxContext fluxContext) {
    Map<String, CompilationVertex.Builder> compilationVertexBuilders = new HashMap<>();
    TopologyDef topologyDef = fluxContext.getTopologyDef();

    // Build the Compilation Graph
    // Add all vertices
    for (SourceDef sourceDef : topologyDef.getSources()) {
      compilationVertexBuilders.put(
          sourceDef.getId(),
          new CompilationVertex.Builder().setVertex(sourceDef));
    }
    for (SinkDef sinkDef : topologyDef.getSinks()) {
      compilationVertexBuilders.put(
          sinkDef.getId(),
          new CompilationVertex.Builder().setVertex(sinkDef));
    }
    for (OperatorDef operatorDef : topologyDef.getOperators()) {
      compilationVertexBuilders.put(
          operatorDef.getId(),
          new CompilationVertex.Builder().setVertex(operatorDef));
    }

    // Add all edges
    for (StreamDef streamDef : topologyDef.getStreams()) {
      compilationVertexBuilders.get(streamDef.getFrom())
          .addOutgoingEdge(streamDef);
      compilationVertexBuilders.get(streamDef.getTo())
          .addIncomingEdge(streamDef);
    }

    for (Map.Entry<String, CompilationVertex.Builder> entry : compilationVertexBuilders.entrySet()) {
      CompilationVertex vertex = entry.getValue().build();
      this.fluxContext.putCompilationVertex(entry.getKey(), vertex);
      if (vertex.readyToCompile()) {
        this.compilationQueue.add(vertex);
      }
    }
  }

  private void compileVertexQueue(StreamExecutionEnvironment senv, FluxContext fluxContext) {
    CompilerImpl compilerImpl = new CompilerImpl();
    while (this.compilationQueue.size() > 0) {
      CompilationVertex vertex = this.compilationQueue.poll();
      compilerImpl.compile(senv, fluxContext, vertex);

      // set downstream vertex compilation flags.
      for (EdgeDef downstreamEdge : vertex.getOutgoingEdge()) {
        CompilationVertex toVertex = this.fluxContext.getCompilationVertex(downstreamEdge.getTo());
        toVertex.addCompiledSourceCount();
        if (toVertex.readyToCompile()) {
          this.compilationQueue.add(toVertex);
        }
      }
    }
  }
}
