package org.apache.flink.flux.compiler;

import org.apache.flink.api.common.JobID;
import org.apache.flink.flux.api.FluxTopology;
import org.apache.flink.flux.model.*;
import org.apache.flink.flux.runtime.FluxTopologyImpl;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Object holder for compilation procedure.
 */
public class CompilationGraph {
    private final FluxContext fluxContext;
    private final StreamExecutionEnvironment senv;
    private Queue<CompilationVertex> compilationQueue = new PriorityQueue<>();

    public CompilationGraph(StreamExecutionEnvironment senv, FluxContext fluxContext) {
        this.senv = senv;
        this.fluxContext = fluxContext;
    }

    public FluxTopology compile() {
        constructCompilationGraph(fluxContext);
        compileVertexQueue(senv, fluxContext);
        JobGraph jobGraph = senv.getStreamGraph().getJobGraph();
        FluxTopologyImpl fluxTopology = new FluxTopologyImpl();
        fluxTopology.setJobGraph(jobGraph);
        return fluxTopology;
    }

    private void constructCompilationGraph(FluxContext fluxContext) {
        Map<String, CompilationVertex.Builder> compilationVertexBuilders = new HashMap<>();
        TopologyDef topologyDef = fluxContext.getTopologyDef();

        // Build the Compilation Graph
        // Add all vertices
        for (SourceDef sourceDef : topologyDef.getSources()) {
            compilationVertexBuilders.put(
                    sourceDef.getId(),
                    new CompilationVertex.Builder().setVertex(sourceDef));
        }
        for (SinkDef sinkDef : topologyDef.getSinks()) {
            compilationVertexBuilders.put(
                    sinkDef.getId(),
                    new CompilationVertex.Builder().setVertex(sinkDef));
        }
        for (OperatorDef operatorDef : topologyDef.getOperators()) {
            compilationVertexBuilders.put(
                    operatorDef.getId(),
                    new CompilationVertex.Builder().setVertex(operatorDef));
        }

        // Add all edges
        for (StreamDef streamDef: topologyDef.getStreams()) {
            compilationVertexBuilders.get(streamDef.getFrom())
                    .addOutgoingEdge(streamDef);
            compilationVertexBuilders.get(streamDef.getTo())
                    .addIncomingEdge(streamDef);
        }

        for (Map.Entry<String, CompilationVertex.Builder> entry : compilationVertexBuilders.entrySet()) {
            CompilationVertex vertex = entry.getValue().build();
            this.fluxContext.putCompilationVertex(entry.getKey(), vertex);
            if (vertex.readyToCompile()) {
                this.compilationQueue.add(vertex);
            }
        }
    }

    private void compileVertexQueue(StreamExecutionEnvironment senv, FluxContext fluxContext) {
        OperatorCompiler operatorCompiler = new OperatorCompiler();
        while (this.compilationQueue.size() > 0) {
            CompilationVertex vertex = this.compilationQueue.poll();
            operatorCompiler.compile(senv, fluxContext, vertex);

            // set downstream vertex compilation flags.
            for (EdgeDef downstreamEdge : vertex.getOutgoingEdge()) {
                CompilationVertex toVertex = this.fluxContext.getCompilationVertex(downstreamEdge.getTo());
                toVertex.addCompiledSourceCount();
                if (toVertex.readyToCompile()) {
                    this.compilationQueue.add(toVertex);
                }
            }
        }
    }
}
