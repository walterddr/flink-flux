package org.apache.flink.flux.topology;

import org.apache.flink.flux.api.FluxTopology;
import org.apache.flink.flux.model.TopologyDef;
import org.apache.flink.flux.parser.FluxParser;
import org.apache.flink.flux.runtime.FluxTopologyBuilderImpl;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class TopologyCompilationTest {

  @Test
  public void testBasicTopologyCompilation() throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/basic_topology.yaml", false, true, null, false);
    topologyDef.validate();
    FluxTopologyBuilderImpl fluxBuilder = FluxTopologyBuilderImpl.createFluxBuilder();
    FluxTopology topology = fluxBuilder.getTopology(topologyDef, null);
    assertNotNull(topology.getJobGraph());
  }

  @Test
  public void testRepartitionTopologyCompilation() throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/repartition_topology.yaml", false, true, null, false);
    topologyDef.validate();
    FluxTopologyBuilderImpl fluxBuilder = FluxTopologyBuilderImpl.createFluxBuilder();
    FluxTopology topology = fluxBuilder.getTopology(topologyDef, null);
    assertNotNull(topology.getJobGraph());
  }
}
