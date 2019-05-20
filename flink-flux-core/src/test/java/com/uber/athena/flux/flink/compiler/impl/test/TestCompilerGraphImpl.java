package com.uber.athena.flux.flink.compiler.impl.test;

import com.uber.athena.flux.flink.compiler.api.Compiler;
import com.uber.athena.flux.flink.compiler.api.CompilerFactory;
import com.uber.athena.flux.flink.compiler.api.CompilerFactoryService;
import com.uber.athena.flux.flink.compiler.context.CompilerContext;
import com.uber.athena.flux.flink.compiler.context.CompilerGraph;
import com.uber.athena.flux.flink.compiler.context.CompilerVertex;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableMap;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

public class TestCompilerGraphImpl extends CompilerGraph {
  private static final Logger LOG = LoggerFactory.getLogger(TestCompilerGraphImpl.class);

  public static final String STREAM_EXEC_ENV = "stream_exec_env";

  private StreamExecutionEnvironment sEnv;
  private CompilerFactory compilerFactory;

  public TestCompilerGraphImpl(
      StreamExecutionEnvironment sEnv,
      CompilerContext compilerContext,
      Class<?> compilerFactoryClass) {
    this.sEnv = sEnv;
    super.compilerContext = compilerContext;
    super.staticProperties = Collections.emptyMap();
    try {
      this.compilerFactory = (CompilerFactory) CompilerFactoryService.find(compilerFactoryClass);
    } catch (ClassNotFoundException e) {
      LOG.error("Cannot find proper compiler for {}", compilerFactoryClass, e);
    }
  }

  @Override
  protected CompilerVertex<?> constructCompilerVertex(CompilerVertex.Builder vertexBuilder) {
    return new TestCompilerVertex(
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
  protected Map<? extends String, ?> findDynamicCompilerProperties(CompilerContext compilerContext, CompilerVertex<?> vertex) {
    return ImmutableMap.of(STREAM_EXEC_ENV, sEnv);
  }

  @Override
  protected Compiler findCompilerForVertex(CompilerVertex<?> vertex) {
    return compilerFactory.getCompiler(vertex.getVertex().getClassName(), Collections.emptyMap());
  }
}
