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

package com.uber.athena.flux.flink.test.utils.sink;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;

/**
 * Test consumer for kafka.
 */
public class KafkaStringSink extends FlinkKafkaProducer010<String> {

  public KafkaStringSink(
      String topic,
      String brokerConnectionString) {
    this(topic, new SimpleStringSchema(), brokerConnectionString);
  }

  public KafkaStringSink(
      String topic,
      SerializationSchema<String> valueSerializer,
      String brokerConnectionString) {
    super(brokerConnectionString, topic, valueSerializer);
  }
}
