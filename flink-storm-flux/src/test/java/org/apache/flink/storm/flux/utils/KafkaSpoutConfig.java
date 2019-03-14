package org.apache.flink.storm.flux.utils;

import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;

import java.util.Collections;
import java.util.Map;

public class KafkaSpoutConfig extends SpoutConfig {
    public Map conf;

  public KafkaSpoutConfig(BrokerHosts hosts, String topic, String zkRoot, String id) {
    super(hosts, topic, zkRoot, id);
    String[] split = ((ZkHosts) hosts).brokerZkStr.split(":");
    this.zkServers = Collections.singletonList(split[0]);
    this.zkPort = Integer.parseInt(split[1]);
  }
}
