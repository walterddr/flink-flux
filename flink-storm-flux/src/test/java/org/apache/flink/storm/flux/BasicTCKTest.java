package org.apache.flink.storm.flux;

import org.apache.flink.storm.flux.parser.FluxParser;
import org.apache.storm.Config;
import org.apache.storm.flux.model.ExecutionContext;
import org.apache.storm.flux.model.TopologyDef;
import org.apache.storm.generated.StormTopology;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class BasicTCKTest {

  @Test
  public void testTCK() throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/tck.yaml", false, true, null, false);
    Config conf = FluxBuilder.buildConfig(topologyDef);
    ExecutionContext context = new ExecutionContext(topologyDef, conf);
    StormTopology topology = FluxBuilder.buildTopology(context);
    assertNotNull(topology);
    topology.validate();
  }
}
