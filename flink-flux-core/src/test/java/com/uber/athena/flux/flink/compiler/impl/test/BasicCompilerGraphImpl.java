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
import com.uber.athena.flux.flink.compiler.api.CompilerFactory;
import com.uber.athena.flux.flink.compiler.api.CompilerFactoryService;
import com.uber.athena.flux.flink.compiler.context.CompilerContext;
import com.uber.athena.flux.flink.compiler.context.CompilerGraph;
import com.uber.athena.flux.flink.compiler.context.CompilerVertex;
import com.uber.athena.flux.model.VertexDef;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableMap;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

public class BasicCompilerGraphImpl extends CompilerGraph {
  public static final String STREAM_EXEC_ENV = "stream_exec_env";
  private static final Logger LOG = LoggerFactory.getLogger(BasicCompilerGraphImpl.class);

  private StreamExecutionEnvironment sEnv;
  private CompilerFactory compilerFactory;

  public BasicCompilerGraphImpl(
      StreamExecutionEnvironment sEnv,
      CompilerContext compilerContext,
      Class<?> compilerFactoryClass) {
    this.sEnv = sEnv;
    this.setCompilerContext(compilerContext);
    this.setStaticProperties(Collections.emptyMap());
    try {
      this.compilerFactory = (CompilerFactory) CompilerFactoryService.find(compilerFactoryClass);
    } catch (ClassNotFoundException e) {
      LOG.error("Cannot find proper compiler for {}", compilerFactoryClass, e);
    }
  }

  @Override
  protected CompilerVertex<?> constructCompilerVertex(CompilerVertex.Builder vertexBuilder) {
    return new BasicCompilerVertex(
        vertexBuilder.getVertex(),
        vertexBuilder.getIncomingEdge(),
        vertexBuilder.getOutgoingEdge()
    );
  }

  @Override
  protected JobGraph constructJobGraphFromCompilerContext() {
    return sEnv.getStreamGraph().getJobGraph();
  }

  @Override
  protected Map<? extends String, ?> findDynamicCompilerProperties(
      CompilerContext compilerContext, CompilerVertex<?> vertex) {
    return ImmutableMap.of(STREAM_EXEC_ENV, sEnv);
  }

  @Override
  protected Compiler findCompilerForVertex(CompilerVertex<?> vertex) {
    return compilerFactory.getCompiler(resolveVertexType(vertex.getVertex()), Collections.emptyMap());
  }

  private static Class<?> resolveVertexType(VertexDef vertex) {
    return null;
  }
}
