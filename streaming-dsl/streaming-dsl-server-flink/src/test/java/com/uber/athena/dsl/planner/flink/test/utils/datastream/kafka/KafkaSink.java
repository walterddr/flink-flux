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

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;

/**
 * This is an example sink extension suitable for DSL modeling.
 *
 * @param <T> the type of data the kafka producer accepts.
 */
public class KafkaSink<T> extends FlinkKafkaProducer011<T> {

  public KafkaSink(
      String topic,
      SerializationSchema<T> serializationSchema,
      KafkaConfig kafkaConfig) {
    super(topic, serializationSchema, kafkaConfig.getProperties());
  }
}
