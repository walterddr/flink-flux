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

import com.uber.athena.flux.model.StreamDef;
import com.uber.athena.flux.model.VertexDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Compiler vertex used within a Flux compilation context.
 *
 * @param <T> type of the compilation results generated.
 */
public abstract class CompilerVertex<T> implements Comparable<CompilerVertex> {
  private VertexDef vertex;
  private List<StreamDef> incomingEdge;
  private List<StreamDef> outgoingEdge;

  private int compiledSourceCount;

  /**
   * Increase compilation flag by one. Used after an upstream vertex has been compiled.
   */
  public void addCompiledSourceCount() {
    this.compiledSourceCount += 1;
  }

  /**
   * Determine whether this vertex is ready for compilation.
   *
   * @return return whether ready to compile.
   */
  public boolean readyToCompile() {
    return this.compiledSourceCount == incomingEdge.size();
  }

  /**
   * Setting the result of the compilation.
   *
   * @param compilationResult the compilation result.
   */
  public abstract void setCompilationResult(T compilationResult);

  /**
   * Getting the result of the compilation, return null if not compiled.
   *
   * @return the compilation result.
   */
  public abstract T getCompilationResult();

  //-------------------------------------------------------------------------
  // Getters
  //-------------------------------------------------------------------------

  public VertexDef getVertex() {
    return vertex;
  }

  public List<StreamDef> getIncomingEdge() {
    return incomingEdge;
  }

  public List<StreamDef> getOutgoingEdge() {
    return outgoingEdge;
  }

  public void setVertex(VertexDef vertex) {
    this.vertex = vertex;
  }

  public void setIncomingEdge(List<StreamDef> incomingEdge) {
    this.incomingEdge = incomingEdge;
  }

  public void setOutgoingEdge(List<StreamDef> outgoingEdge) {
    this.outgoingEdge = outgoingEdge;
  }

  public int getCompiledSourceCount() {
    return compiledSourceCount;
  }

  public void setCompiledSourceCount(int compiledSourceCount) {
    this.compiledSourceCount = compiledSourceCount;
  }

  @Override
  public int compareTo(CompilerVertex that) {
    return this.getCompiledSourceCount() - that.getCompiledSourceCount();
  }

  // ------------------------------------------------------------------------
  // Builder pattern
  // ------------------------------------------------------------------------

  /**
   * Builder for the Flux compiler suite.
   */
  public static class Builder {
    private VertexDef vertex;
    private List<StreamDef> incomingEdge = new ArrayList<>();
    private List<StreamDef> outgoingEdge = new ArrayList<>();

    public Builder() {
    }

    public Builder setVertex(VertexDef vertexDef) {
      this.vertex = vertexDef;
      return this;
    }

    public Builder addIncomingEdge(StreamDef streamDef) {
      this.incomingEdge.add(streamDef);
      return this;
    }

    public Builder addOutgoingEdge(StreamDef streamDef) {
      this.outgoingEdge.add(streamDef);
      return this;
    }

    public VertexDef getVertex() {
      return vertex;
    }

    public List<StreamDef> getIncomingEdge() {
      return incomingEdge;
    }

    public List<StreamDef> getOutgoingEdge() {
      return outgoingEdge;
    }
  }
}
