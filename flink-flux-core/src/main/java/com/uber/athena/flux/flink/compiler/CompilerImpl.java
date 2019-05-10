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

import com.uber.athena.flux.model.OperatorDef;
import com.uber.athena.flux.model.SinkDef;
import com.uber.athena.flux.model.SourceDef;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Preconditions;

import static com.uber.athena.flux.flink.compiler.utils.CompilationUtils.compileOperator;
import static com.uber.athena.flux.flink.compiler.utils.CompilationUtils.compileSink;
import static com.uber.athena.flux.flink.compiler.utils.CompilationUtils.compileSource;

/**
 * Compiler implementation for operator-level Flux compilation.
 */
public class CompilerImpl implements Compiler {

  public CompilerImpl() {
  }

  /**
   * Compile a single vertex into chaining datastream.
   * @param senv        stream execution environment
   * @param fluxContext flux context
   * @param vertex      compilation vertex.
   */
  @Override
  public void compile(StreamExecutionEnvironment senv, FluxContext fluxContext, CompilationVertex vertex) {
    Preconditions.checkArgument(vertex.readyToCompile());
    try {
      if (vertex.getVertex() instanceof SourceDef) {
        compileSource(fluxContext, senv, vertex);
      } else if (vertex.getVertex() instanceof OperatorDef) {
        compileOperator(fluxContext, vertex);
      } else if (vertex.getVertex() instanceof SinkDef) {
        compileSink(fluxContext, vertex);
      }
    } catch (Exception e) {
      throw new RuntimeException("Cannot compile vertex: " + vertex.getVertex().getId(), e);
    }
  }
}
