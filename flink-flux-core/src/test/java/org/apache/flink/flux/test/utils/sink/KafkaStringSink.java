package org.apache.flink.flux.test.utils.sink;

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
