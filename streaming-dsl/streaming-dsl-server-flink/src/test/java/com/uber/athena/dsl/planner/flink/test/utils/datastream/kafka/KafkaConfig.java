/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.uber.athena.dsl.planner.flink.test.utils.datastream.kafka;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration entity for Kafka cluster.
 *
 * <p>This class is used to demonstrate how to utilize object reference and
 * dereference from DSL modeling.
 */
public class KafkaConfig {

  private final List<String> brokerList;
  private final List<String> zookeeperList;
  private final Properties configProperties;

  public KafkaConfig(
      List<String> brokerList,
      List<String> zookeeperList
  ) {
    this(brokerList, zookeeperList, new Properties());
  }

  public KafkaConfig(
      List<String> brokerList,
      List<String> zookeeperList,
      Map<Object, Object> configMap
  ) {
    this.brokerList = brokerList;
    this.zookeeperList = zookeeperList;
    this.configProperties = new Properties();
    this.configProperties.putAll(configMap);
  }

  public KafkaConfig(
      List<String> brokerList,
      List<String> zookeeperList,
      Properties configProperties
  ) {
    this.brokerList = brokerList;
    this.zookeeperList = zookeeperList;
    this.configProperties = configProperties;
  }

  public Properties getProperties() {
    Properties props = new Properties();

    // Putting in default properties
    props.setProperty("zookeeper.connect", String.join(",", zookeeperList));
    props.setProperty("bootstrap.servers", String.join(",", brokerList));
    props.setProperty("group.id", "default.consumer.group.id");
    props.setProperty("enable.auto.commit", "false");
    props.setProperty("zookeeper.session.timeout.ms", String.valueOf(1000));
    props.setProperty("zookeeper.connection.timeout.ms", String.valueOf(1000));
    // read from the latest.
    props.setProperty("auto.offset.reset", "latest");
    // make a lot of fetches (MESSAGES MUST BE SMALLER!)
    props.setProperty("max.partition.fetch.bytes", "256");
    // override by config properties.
    props.putAll(configProperties);
    return props;
  }
}
