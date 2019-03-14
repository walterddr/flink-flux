package org.apache.flink.storm.flux.utils;


import java.io.Serializable;

public class KafkaBoltConfig implements Serializable {
  public final String topic;
  public final String hosts;
  public final String id;

  public KafkaBoltConfig(String hosts, String topic, String id) {
    this.hosts = hosts;
    this.id = id;
    this.topic = topic;
  }
}
