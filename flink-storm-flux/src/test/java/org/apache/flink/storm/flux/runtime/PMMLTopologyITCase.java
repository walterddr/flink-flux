package org.apache.flink.storm.flux.runtime;

import org.apache.flink.storm.api.FlinkClient;
import org.apache.flink.storm.api.FlinkTopology;
import org.apache.flink.storm.flux.FluxBuilder;
import org.apache.flink.storm.flux.model.ExecutionContext;
import org.apache.flink.storm.flux.model.TopologyDef;
import org.apache.flink.storm.flux.parser.FluxParser;
import org.apache.storm.Config;
import org.apache.storm.topology.TopologyBuilder;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

public class PMMLTopologyITCase {

  @Test
  public void testPMMLBasicTopologyExec() throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/pmml_basic_topology.yaml", false, true, null, false);
    Config conf = FluxBuilder.buildConfig(topologyDef);

    ExecutionContext context = new ExecutionContext(topologyDef, conf);
    TopologyBuilder builder = FluxBuilder.createTopologyBuilder(context);
    FlinkTopology topology = FlinkTopology.createTopology(builder);
    assertNotNull(topology);

    // No extra configuration needed at this moment as a single parallelism task
    Map extraConf = new HashMap();
    conf.putAll(extraConf);

    FlinkClient.addStormConfigToTopology(topology, extraConf);
    topology.execute();
  }
}
