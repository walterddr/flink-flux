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
 */

package org.apache.flink.flux.test.utils.source;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.Properties;

/**
 * Test consumer for kafka.
 */
public class KafkaStringSource extends FlinkKafkaConsumer010<String> {
  private static final String DEFAULT_ZK_TIMEOUT = "30000";
  private static final String DEFAULT_START_OFFSET = "earliest";

  public KafkaStringSource(
      String topic,
      String zookeeperConnectionString,
      String brokerConnectionString) {
    this(topic,
        zookeeperConnectionString,
        brokerConnectionString,
        DEFAULT_ZK_TIMEOUT,
        DEFAULT_START_OFFSET
    );
  }

  public KafkaStringSource(
      String topic,
      String zookeeperConnectionString,
      String brokerConnectionString,
      String zkTimeout,
      String startOffset) {
    this(topic, new SimpleStringSchema(), constructStandardProps(
        zookeeperConnectionString,
        brokerConnectionString,
        zkTimeout,
        startOffset
    ));
  }

  public KafkaStringSource(
      String topic,
      DeserializationSchema<String> valueDeserializer,
      Properties props) {
    super(topic, valueDeserializer, props);
  }

  private static Properties constructStandardProps(
      String zookeeperConnectionString,
      String brokerConnectionString,
      String zkTimeout,
      String startOffset) {
    Properties standardProps = new Properties();
    standardProps.setProperty("zookeeper.connect", zookeeperConnectionString);
    standardProps.setProperty("bootstrap.servers", brokerConnectionString);
    standardProps.setProperty("group.id", "flink-tests");
    standardProps.setProperty("enable.auto.commit", "false");
    standardProps.setProperty("zookeeper.session.timeout.ms", zkTimeout);
    standardProps.setProperty("zookeeper.connection.timeout.ms", zkTimeout);
    // read from the beginning. (earliest is kafka 0.9 value)
    standardProps.setProperty("auto.offset.reset", startOffset);
    // make a lot of fetches (MESSAGES MUST BE SMALLER!)
    standardProps.setProperty("max.partition.fetch.bytes", "256");
    return standardProps;
  }
}
