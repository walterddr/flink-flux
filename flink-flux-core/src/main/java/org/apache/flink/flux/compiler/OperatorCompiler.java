package org.apache.flink.flux.compiler;

import org.apache.flink.flux.model.OperatorDef;
import org.apache.flink.flux.model.SinkDef;
import org.apache.flink.flux.model.SourceDef;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.flink.flux.compiler.utils.CompilationUtils.*;

/**
 * Compile
 */
public class OperatorCompiler implements Compiler {
    private static Logger LOG = LoggerFactory.getLogger(OperatorCompiler.class);

    public OperatorCompiler() {
    }

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
