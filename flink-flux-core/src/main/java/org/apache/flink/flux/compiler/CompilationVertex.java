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

package org.apache.flink.flux.compiler;

import org.apache.flink.flux.model.EdgeDef;
import org.apache.flink.flux.model.VertexDef;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.ArrayList;
import java.util.List;

public class CompilationVertex implements Comparable<CompilationVertex> {

  private final VertexDef vertex;
  private final List<EdgeDef> incomingEdge;
  private final List<EdgeDef> outgoingEdge;

  private int compiledSourceCount;
  private DataStream dataStream = null;

  private CompilationVertex(VertexDef vertex, List<EdgeDef> incomingEdge, List<EdgeDef> outgoingEdge) {
    this.vertex = vertex;
    this.incomingEdge = incomingEdge;
    this.outgoingEdge = outgoingEdge;
    this.compiledSourceCount = 0;
  }

  /**
   * Increase compilation flag by one. Used after an upstream vertex has been compiled.
   */
  public void addCompiledSourceCount() {
    this.compiledSourceCount += 1;
  }

  /**
   * Determine whether this vertex is ready for compilation.
   *
   * @return
   */
  public boolean readyToCompile() {
    return this.compiledSourceCount == incomingEdge.size();
  }

  /**
   * Get vertex
   *
   * @return vertex definition
   */
  public VertexDef getVertex() {
    return vertex;
  }

  //-------------------------------------------------------------------------
  // Getters
  //-------------------------------------------------------------------------

  public List<EdgeDef> getIncomingEdge() {
    return incomingEdge;
  }

  public List<EdgeDef> getOutgoingEdge() {
    return outgoingEdge;
  }

  public DataStream getDataStream() {
    return dataStream;
  }

  public void setDataStream(DataStream dataStream) {
    this.dataStream = dataStream;
  }

  /**
   * comparator used during priority queue compilation
   *
   * @param that other object.
   * @return compilation object vs incoming edge differences. smaller has priority.
   */
  @Override
  public int compareTo(CompilationVertex that) {
    return (this.compiledSourceCount - this.incomingEdge.size()) -
        (that.compiledSourceCount - that.incomingEdge.size());
  }


  // ------------------------------------------------------------------------
  // Builder pattern
  // ------------------------------------------------------------------------

  /**
   * Builder for the Flux compiler suite.
   */
  public static class Builder {
    private VertexDef vertex;
    private List<EdgeDef> incomingEdge = new ArrayList<>();
    private List<EdgeDef> outgoingEdge = new ArrayList<>();

    public Builder() {
    }

    public Builder setVertex(VertexDef vertexDef) {
      this.vertex = vertexDef;
      return this;
    }

    public Builder addIncomingEdge(EdgeDef edgeDef) {
      this.incomingEdge.add(edgeDef);
      return this;
    }

    public Builder addOutgoingEdge(EdgeDef edgeDef) {
      this.outgoingEdge.add(edgeDef);
      return this;
    }

    public CompilationVertex build() {
      return new CompilationVertex(
          vertex,
          incomingEdge,
          outgoingEdge);
    }
  }
}
