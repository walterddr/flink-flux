package org.apache.flink.storm.flux.runtime;

import org.apache.flink.storm.api.FlinkClient;
import org.apache.flink.storm.api.FlinkTopology;
import org.apache.flink.storm.flux.FluxBuilder;
import org.apache.flink.storm.flux.parser.FluxParser;
import org.apache.storm.Config;
import org.apache.storm.flux.model.ExecutionContext;
import org.apache.storm.flux.model.TopologyDef;
import org.apache.storm.topology.TopologyBuilder;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

public class KafkaTopologyITCase {

  @Test
  public void testBasicKafkaTopologyExec() throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/basic_kafka_topology.yaml", false, true, null, false);
    Config conf = FluxBuilder.buildConfig(topologyDef);

    ExecutionContext context = new ExecutionContext(topologyDef, conf);
    TopologyBuilder builder = FluxBuilder.createTopologyBuilder(context);
    FlinkTopology topology = FlinkTopology.createTopology(builder);
    assertNotNull(topology);

    Map extraConf = new HashMap();
    extraConf.put(Config.STORM_ZOOKEEPER_SESSION_TIMEOUT, 1000);
    extraConf.put(Config.STORM_ZOOKEEPER_CONNECTION_TIMEOUT, 1000);
    extraConf.put(Config.STORM_ZOOKEEPER_RETRY_TIMES, 4);
    extraConf.put(Config.STORM_ZOOKEEPER_RETRY_INTERVAL, 5);
    conf.putAll(extraConf);

    FlinkClient.addStormConfigToTopology(topology, extraConf);
    topology.execute();
  }
}
