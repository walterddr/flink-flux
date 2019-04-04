package org.apache.flink.flux.runtime;

import org.apache.flink.flux.api.FluxTopology;
import org.apache.flink.flux.model.TopologyDef;
import org.apache.flink.flux.parser.FluxParser;
import org.junit.Test;

public class TopologyRuntimeITCase {

    @Test
    public void testBasicTopologyRuntime() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/basic_topology.yaml", false, true, null, false);
        topologyDef.validate();
        FluxTopologyBuilderImpl fluxBuilder = FluxTopologyBuilderImpl.createFluxBuilder();
        FluxTopology topology = fluxBuilder.getTopology(topologyDef, null);
        fluxBuilder.execute(topology);
    }

    @Test
    public void testRepartitionTopologyRuntime() throws Exception {
        TopologyDef topologyDef = FluxParser.parseResource("/configs/repartition_topology.yaml", false, true, null, false);
        topologyDef.validate();
        FluxTopologyBuilderImpl fluxBuilder = FluxTopologyBuilderImpl.createFluxBuilder();
        FluxTopology topology = fluxBuilder.getTopology(topologyDef, null);
        fluxBuilder.execute(topology);
    }
}
