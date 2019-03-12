package org.apache.flink.storm.flux;

import org.apache.flink.storm.api.FlinkTopology;
import org.apache.flink.storm.flux.parser.FluxParser;
import org.apache.storm.Config;
import org.apache.storm.flux.model.ExecutionContext;
import org.apache.storm.flux.model.TopologyDef;
import org.apache.storm.topology.TopologyBuilder;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class BasicTopologyTest {

  @Test
  public void testBasicTopologyGen() throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/basic_topology.yaml", false, true, null, false);
    Config conf = FluxBuilder.buildConfig(topologyDef);
    ExecutionContext context = new ExecutionContext(topologyDef, conf);
    TopologyBuilder builder = FluxBuilder.createTopologyBuilder(context);
    FlinkTopology topology = FlinkTopology.createTopology(builder);
    assertNotNull(topology);
  }

  @Test
  public void testRepartitionTopologyGen() throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/repartition_topology.yaml", false, true, null, false);
    Config conf = FluxBuilder.buildConfig(topologyDef);
    ExecutionContext context = new ExecutionContext(topologyDef, conf);
    TopologyBuilder builder = FluxBuilder.createTopologyBuilder(context);
    FlinkTopology topology = FlinkTopology.createTopology(builder);
    assertNotNull(topology);
  }
}
