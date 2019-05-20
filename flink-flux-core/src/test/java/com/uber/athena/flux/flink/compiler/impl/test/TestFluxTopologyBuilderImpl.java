package com.uber.athena.flux.flink.compiler.impl.test;

import com.uber.athena.flux.flink.compiler.context.CompilerContext;
import com.uber.athena.flux.flink.compiler.context.CompilerGraph;
import com.uber.athena.flux.flink.compiler.impl.test.factory.TestCompilerFactory;
import com.uber.athena.flux.flink.compiler.runtime.FlinkFluxTopology;
import com.uber.athena.flux.flink.compiler.runtime.FlinkFluxTopologyBuilder;
import com.uber.athena.flux.model.TopologyDef;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Preconditions;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class TestFluxTopologyBuilderImpl extends FlinkFluxTopologyBuilder {

  private final TopologyDef topologyDef;
  private final StreamExecutionEnvironment streamExecutionEnvironment;
  private final CompilerContext compilerContext;
  private CompilerGraph compilerGraph;

  public TestFluxTopologyBuilderImpl(
      TopologyDef topologyDef,
      StreamExecutionEnvironment streamExecutionEnvironment) {
    this(topologyDef, streamExecutionEnvironment,
        generateFlinkConfiguration(Collections.emptyMap()));
  }

  public TestFluxTopologyBuilderImpl(
      TopologyDef topologyDef,
      StreamExecutionEnvironment streamExecutionEnvironment,
      Configuration config) {
    this.streamExecutionEnvironment = streamExecutionEnvironment;
    this.topologyDef = topologyDef;
    this.compilerContext = new CompilerContext(topologyDef, config);
    this.compilerGraph = new TestCompilerGraphImpl(
        this.streamExecutionEnvironment,
        this.compilerContext,
        TestCompilerFactory.class) {
    };
  }

  /**
   * compile topology definition to {@code FluxTopology}.
   *
   * <p>The compilation should invoke the compilation framework based on
   * constructed settings.
   *
   * @return a flux topology, different compilation suits might return different implementations.
   */
  @Override
  public FlinkFluxTopology createTopology(TopologyDef topologyDef, Map<String, Object> config) throws IOException {
    Preconditions.checkNotNull(topologyDef, "topology cannot be null!");
    Preconditions.checkNotNull(streamExecutionEnvironment, "execution environment cannot be null!");
    return this.compileInternal();
  }

  private FlinkFluxTopology compileInternal() {
    return this.compilerGraph.compile();
  }
}
