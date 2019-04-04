package org.apache.flink.flux.compiler;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.flux.api.FluxTopology;
import org.apache.flink.flux.model.TopologyDef;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Preconditions;

import java.util.HashMap;
import java.util.Map;

/**
 * Compilation framework for the flux topology.
 */
public class FluxCompilerSuite {

    private final TopologyDef topologyDef;
    private final Configuration config;
    private final StreamExecutionEnvironment streamExecutionEnvironment;
    private final FluxContext fluxContext;

    private CompilationGraph compilationGraph;

    public FluxCompilerSuite(
            TopologyDef topologyDef,
            Configuration config,
            StreamExecutionEnvironment streamExecutionEnvironment) {
        this.streamExecutionEnvironment = streamExecutionEnvironment;
        this.topologyDef = topologyDef;
        this.config = new Configuration(config);
        this.fluxContext = new FluxContext(topologyDef, config);
        this.compilationGraph = new CompilationGraph(
                this.streamExecutionEnvironment,
                this.fluxContext);
    }

    /**
     * compile topology definition to {@link FluxTopology}.
     * @return
     */
    public FluxTopology compile() {
        Preconditions.checkNotNull(topologyDef, "topology cannot be null!");
        Preconditions.checkNotNull(streamExecutionEnvironment, "execution environment cannot be null!");
        return this.compileInternal();
    }

    private FluxTopology compileInternal() {
        return this.compilationGraph.compile();
    }
}
