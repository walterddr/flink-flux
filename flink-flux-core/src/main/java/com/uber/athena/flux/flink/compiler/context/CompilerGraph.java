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

package com.uber.athena.flux.flink.compiler.context;

import com.uber.athena.flux.flink.compiler.api.Compiler;
import com.uber.athena.flux.flink.compiler.runtime.FlinkFluxTopology;
import com.uber.athena.flux.model.EdgeDef;
import com.uber.athena.flux.model.OperatorDef;
import com.uber.athena.flux.model.SinkDef;
import com.uber.athena.flux.model.SourceDef;
import com.uber.athena.flux.model.StreamDef;
import com.uber.athena.flux.model.TopologyDef;
import com.uber.athena.flux.utils.TopologyUtils;
import org.apache.flink.runtime.jobgraph.JobGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Object holder for compilation procedure.
 */
public abstract class CompilerGraph {
  private Map<String, Object> staticProperties;
  private CompilerContext compilerContext;
  private Queue<CompilerVertex<?>> compilationQueue = new PriorityQueue<>();

  /**
   * Compile current graph into a {@code FluxTopology}.
   *
   * @return the topology
   */
  public FlinkFluxTopology compile() {
    constructCompilationGraph(compilerContext);
    compileVertexQueue(compilerContext, staticProperties);
    JobGraph jobGraph = constructJobGraphFromCompilerContext();
    FlinkFluxTopology fluxTopology = new FlinkFluxTopology();
    fluxTopology.setJobGraph(jobGraph);
    return fluxTopology;
  }

  protected abstract CompilerVertex<?> constructCompilerVertex(CompilerVertex.Builder vertexBuilder);

  protected abstract JobGraph constructJobGraphFromCompilerContext();

  protected abstract Map<? extends String, ?> findDynamicCompilerProperties(
      CompilerContext compilerContext, CompilerVertex<?> vertex);

  protected abstract Compiler findCompilerForVertex(CompilerVertex<?> vertex);

  private void constructCompilationGraph(CompilerContext compilerContext) {
    Map<String, CompilerVertex.Builder> compilationVertexBuilders = new HashMap<>();
    TopologyDef topologyDef = compilerContext.getTopologyDef();

    // Build the Compilation Graph
    // Add all vertices
    for (SourceDef sourceDef : TopologyUtils.getSourceList(topologyDef)) {
      compilationVertexBuilders.put(
          sourceDef.getId(),
          new CompilerVertex.Builder().setVertex(sourceDef));
    }
    for (SinkDef sinkDef : TopologyUtils.getSinkList(topologyDef)) {
      compilationVertexBuilders.put(
          sinkDef.getId(),
          new CompilerVertex.Builder().setVertex(sinkDef));
    }
    for (OperatorDef operatorDef : TopologyUtils.getOperatorList(topologyDef)) {
      compilationVertexBuilders.put(
          operatorDef.getId(),
          new CompilerVertex.Builder().setVertex(operatorDef));
    }

    // Add all edges
    for (StreamDef streamDef : topologyDef.getStreams()) {
      compilationVertexBuilders.get(streamDef.getFromVertex())
          .addOutgoingEdge(streamDef);
      compilationVertexBuilders.get(streamDef.getToVertex())
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

  private void compileVertexQueue(CompilerContext compilerContext, Map<String, Object> staticProperties) {
    while (this.compilationQueue.size() > 0) {
      CompilerVertex<?> vertex = this.compilationQueue.poll();
      Compiler<?> compiler = findCompilerForVertex(vertex);
      Map<String, Object> properties = new HashMap<>();
      properties.putAll(staticProperties);
      properties.putAll(findDynamicCompilerProperties(compilerContext, vertex));
      compiler.compile(compilerContext, properties, vertex);

      // set downstream vertex compilation flags.
      for (EdgeDef downstreamEdge : vertex.getOutgoingEdge()) {
        CompilerVertex toVertex = this.compilerContext.getCompilationVertex(downstreamEdge.getToVertex());
        toVertex.addCompiledSourceCount();
        if (toVertex.readyToCompile()) {
          this.compilationQueue.add(toVertex);
        }
      }
    }
  }

  public void setStaticProperties(Map<String, Object> staticProperties) {
    this.staticProperties = staticProperties;
  }

  public void setCompilerContext(CompilerContext compilerContext) {
    this.compilerContext = compilerContext;
  }
}
