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

package com.uber.athena.flux.flink.compiler.api;

import com.uber.athena.flux.flink.compiler.impl.datastream.DataStreamCompilerImpl;
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
public abstract class CompilerGraph {
  protected CompilerContext compilerContext;
  protected StreamExecutionEnvironment senv;
  protected Queue<CompilerVertex<?>> compilationQueue = new PriorityQueue<>();

  /**
   * Compile current graph into a {@code FluxTopology}.
   *
   * @return the topology
   */
  public FluxTopologyImpl compile() {
    constructCompilationGraph(compilerContext);
    compileVertexQueue(senv, compilerContext);
    JobGraph jobGraph = senv.getStreamGraph().getJobGraph();
    FluxTopologyImpl fluxTopology = new FluxTopologyImpl(senv);
    fluxTopology.setJobGraph(jobGraph);
    return fluxTopology;
  }

  public abstract CompilerVertex<?> constructCompilerVertex(CompilerVertex.Builder vertexBuilder);

  private void constructCompilationGraph(CompilerContext compilerContext) {
    Map<String, CompilerVertex.Builder> compilationVertexBuilders = new HashMap<>();
    TopologyDef topologyDef = compilerContext.getTopologyDef();

    // Build the Compilation Graph
    // Add all vertices
    for (SourceDef sourceDef : topologyDef.getSources()) {
      compilationVertexBuilders.put(
          sourceDef.getId(),
          new CompilerVertex.Builder().setVertex(sourceDef));
    }
    for (SinkDef sinkDef : topologyDef.getSinks()) {
      compilationVertexBuilders.put(
          sinkDef.getId(),
          new CompilerVertex.Builder().setVertex(sinkDef));
    }
    for (OperatorDef operatorDef : topologyDef.getOperators()) {
      compilationVertexBuilders.put(
          operatorDef.getId(),
          new CompilerVertex.Builder().setVertex(operatorDef));
    }

    // Add all edges
    for (StreamDef streamDef : topologyDef.getStreams()) {
      compilationVertexBuilders.get(streamDef.getFrom())
          .addOutgoingEdge(streamDef);
      compilationVertexBuilders.get(streamDef.getTo())
          .addIncomingEdge(streamDef);
    }

    for (Map.Entry<String, CompilerVertex.Builder> entry : compilationVertexBuilders.entrySet()) {
      CompilerVertex vertex = constructCompilerVertex(entry.getValue());
      this.compilerContext.putCompilationVertex(entry.getKey(), vertex);
      if (vertex.readyToCompile()) {
        this.compilationQueue.add(vertex);
      }
    }
  }

  private void compileVertexQueue(StreamExecutionEnvironment senv, CompilerContext compilerContext) {
    DataStreamCompilerImpl dataStreamCompilerImpl = new DataStreamCompilerImpl();
    while (this.compilationQueue.size() > 0) {
      CompilerVertex<?> vertex = this.compilationQueue.poll();
      dataStreamCompilerImpl.compile(senv, compilerContext, vertex);

      // set downstream vertex compilation flags.
      for (EdgeDef downstreamEdge : vertex.getOutgoingEdge()) {
        CompilerVertex toVertex = this.compilerContext.getCompilationVertex(downstreamEdge.getTo());
        toVertex.addCompiledSourceCount();
        if (toVertex.readyToCompile()) {
          this.compilationQueue.add(toVertex);
        }
      }
    }
  }
}