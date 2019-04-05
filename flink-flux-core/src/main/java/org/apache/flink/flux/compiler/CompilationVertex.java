package org.apache.flink.flux.compiler;

import org.apache.flink.flux.model.EdgeDef;
import org.apache.flink.flux.model.VertexDef;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.ArrayList;
import java.util.List;

public class CompilationVertex implements Comparable<CompilationVertex> {

    private final VertexDef vertex;
    private final List<EdgeDef> incomingEdge;
    private final List<EdgeDef> outgoingEdge;

    private int compiledSourceCount;
    private DataStream dataStream = null;

    private CompilationVertex(VertexDef vertex, List<EdgeDef> incomingEdge, List<EdgeDef> outgoingEdge) {
        this.vertex = vertex;
        this.incomingEdge = incomingEdge;
        this.outgoingEdge = outgoingEdge;
        this.compiledSourceCount = 0;
    }

    /**
     * Increase compilation flag by one. Used after an upstream vertex has been compiled.
     */
    public void addCompiledSourceCount() {
        this.compiledSourceCount += 1;
    }

    /**
     * Set the dataStream, Used after the current vertex is compiled.
     * @param dataStream compiled dataStream.
     */
    public void setDataStream(DataStream dataStream) {
        this.dataStream = dataStream;
    }

    /**
     * Determine whether this vertex is ready for compilation.
     * @return
     */
    public boolean readyToCompile() {
        return this.compiledSourceCount == incomingEdge.size();
    }

    //-------------------------------------------------------------------------
    // Getters
    //-------------------------------------------------------------------------

    /**
     * Get vertex
     * @return vertex definition
     */
    public VertexDef getVertex() {
        return vertex;
    }

    /**
     * Get incoming edge
     * @return edge definition
     */
    public List<EdgeDef> getIncomingEdge() {
        return incomingEdge;
    }

    /**
     * Get outgoing edge
     * @return edge definition
     */
    public List<EdgeDef> getOutgoingEdge() {
        return outgoingEdge;
    }

    /**
     * Get compiled datastream
     * @return data stream definition
     */
    public DataStream getDataStream() {
        return dataStream;
    }

    /**
     * comparator used during priority queue compilation
     * @param that other object.
     * @return compilation object vs incoming edge differences. smaller has priority.
     */
    @Override
    public int compareTo(CompilationVertex that) {
        return (this.compiledSourceCount - this.incomingEdge.size()) -
                (that.compiledSourceCount - that.incomingEdge.size());
    }


    // ------------------------------------------------------------------------
    // Builder pattern
    // ------------------------------------------------------------------------

    /**
     * Builder for the Flux compiler suite.
     */
    public static class Builder {
        private VertexDef vertex;
        private List<EdgeDef> incomingEdge = new ArrayList<>();
        private List<EdgeDef> outgoingEdge = new ArrayList<>();

        public Builder() {
        }

        public Builder setVertex(VertexDef vertexDef) {
            this.vertex = vertexDef;
            return this;
        }

        public Builder addIncomingEdge(EdgeDef edgeDef) {
            this.incomingEdge.add(edgeDef);
            return this;
        }

        public Builder addOutgoingEdge(EdgeDef edgeDef) {
            this.outgoingEdge.add(edgeDef);
            return this;
        }

        public CompilationVertex build(){
            return new CompilationVertex(
                    vertex,
                    incomingEdge,
                    outgoingEdge);
        }
    }
}
