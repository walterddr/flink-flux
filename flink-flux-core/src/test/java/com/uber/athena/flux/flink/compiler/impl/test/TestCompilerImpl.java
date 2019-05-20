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

import com.uber.athena.flux.flink.compiler.api.Compiler;
import com.uber.athena.flux.flink.compiler.context.CompilerContext;
import com.uber.athena.flux.flink.compiler.context.CompilerVertex;
import com.uber.athena.flux.model.OperatorDef;
import com.uber.athena.flux.model.SinkDef;
import com.uber.athena.flux.model.SourceDef;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Preconditions;

import java.util.Map;

import static com.uber.athena.flux.flink.compiler.impl.test.TestCompilerGraphImpl.STREAM_EXEC_ENV;
import static com.uber.athena.flux.flink.compiler.impl.test.utils.TestCompilationUtils.compileOperator;
import static com.uber.athena.flux.flink.compiler.impl.test.utils.TestCompilationUtils.compileSink;
import static com.uber.athena.flux.flink.compiler.impl.test.utils.TestCompilationUtils.compileSource;

/**
 * Compiler implementation for operator-level Flux compilation.
 */
public class TestCompilerImpl implements Compiler<DataStream> {

  public TestCompilerImpl() {
  }

  /**
   * Compile a single vertex into chaining datastream.
   * @param compilerContext flux context
   * @param properties additional properties required for compilation
   * @param vertex compilation vertex
   */
  @Override
  public void compile(
      CompilerContext compilerContext,
      Map<String, Object> properties,
      CompilerVertex vertex) {
    Preconditions.checkArgument(vertex.readyToCompile());
    StreamExecutionEnvironment sEnv = (StreamExecutionEnvironment)
        Preconditions.checkNotNull(properties.get(STREAM_EXEC_ENV));
    try {
      if (vertex.getVertex() instanceof SourceDef) {
        compileSource(compilerContext, sEnv, vertex);
      } else if (vertex.getVertex() instanceof OperatorDef) {
        compileOperator(compilerContext, vertex);
      } else if (vertex.getVertex() instanceof SinkDef) {
        compileSink(compilerContext, vertex);
      }
    } catch (Exception e) {
      throw new RuntimeException("Cannot compile vertex: " + vertex.getVertex().getId(), e);
    }
  }
}
