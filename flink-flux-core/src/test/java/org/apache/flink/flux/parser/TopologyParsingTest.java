package org.apache.flink.flux.parser;

import org.apache.flink.flux.model.TopologyDef;
import org.junit.Test;

public class TopologyParsingTest {

  @Test
  public void testBasicTopologyGen() throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/basic_topology.yaml", false, true, null, false);
    topologyDef.validate();
  }

  @Test
  public void testRepartitionTopologyGen() throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/repartition_topology.yaml", false, true, null, false);
    topologyDef.validate();
  }
}
