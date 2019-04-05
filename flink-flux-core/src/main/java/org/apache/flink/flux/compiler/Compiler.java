package org.apache.flink.flux.compiler;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Compile a specific component into executable DataStream elements.
 *
 * <p>
 * This compiler main interface does not provide any concrete compilation interface
 * as the actual compilation result varies depends on the API level selected.
 *
 * This interface is only used as the based component of all compilation extensions.
 * </p>
 */
public interface Compiler {

    /**
     * Compile the thing.
     *
     * @param senv stream execution environment
     * @param fluxContext flux context
     * @param vertex compilation vertex.
     */
    void compile(StreamExecutionEnvironment senv, FluxContext fluxContext, CompilationVertex vertex);
}
