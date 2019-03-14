package org.apache.flink.storm.flux.bolt;

import org.apache.flink.storm.flux.utils.KafkaBoltConfig;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.kafka.bolt.selector.KafkaTopicSelector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.TupleUtils;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaStringBolt extends KafkaBolt<String, String> {
  private Properties boltSpecifiedProperties = new Properties();
  private KafkaBoltConfig boltConfig;

  private OutputCollector collector;
  private KafkaProducer<String, String> producer;
  private KafkaTopicSelector topicSelector;
  private boolean fireAndForget = false;
  private boolean async = true;

  public KafkaStringBolt(KafkaBoltConfig boltConfig) {
    Properties props = new Properties();
    props.put("acks", "1");
    props.put("bootstrap.servers", boltConfig.hosts);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("metadata.fetch.timeout.ms", 1000);
    this.boltConfig = boltConfig;
    this.boltSpecifiedProperties = props;
  }

  public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    if (this.topicSelector == null) {
      this.topicSelector = new DefaultTopicSelector(boltConfig.topic);
    }

    this.producer = new KafkaProducer<>(this.boltSpecifiedProperties);
    this.collector = collector;
  }

  public void execute(final Tuple input) {
    if (TupleUtils.isTick(input)) {
      this.collector.ack(input);
    } else {
      String key = null;
      String message = null;
      String topic = null;

      try {
        key = input.toString();
        message = input.toString();
        topic = this.topicSelector.getTopic(input);
        if (topic != null) {
          Callback callback = null;
          if (!this.fireAndForget && this.async) {
            callback = new Callback() {
              public void onCompletion(RecordMetadata ignored, Exception e) {
                synchronized(KafkaStringBolt.this.collector) {
                  if (e != null) {
                    KafkaStringBolt.this.collector.reportError(e);
                    KafkaStringBolt.this.collector.fail(input);
                  } else {
                    KafkaStringBolt.this.collector.ack(input);
                  }

                }
              }
            };
          }

          Future<RecordMetadata> result = this.producer.send(new ProducerRecord(topic, key, message), callback);
          if (!this.async) {
            try {
              result.get();
              this.collector.ack(input);
            } catch (ExecutionException var8) {
              this.collector.reportError(var8);
              this.collector.fail(input);
            }
          } else if (this.fireAndForget) {
            this.collector.ack(input);
          }
        } else {
          this.collector.ack(input);
        }
      } catch (Exception var9) {
        this.collector.reportError(var9);
        this.collector.fail(input);
      }

    }
  }

  public void declareOutputFields(OutputFieldsDeclarer declarer) {
  }

  public void cleanup() {
    this.producer.close();
  }

  public void setFireAndForget(boolean fireAndForget) {
    this.fireAndForget = fireAndForget;
  }

  public void setAsync(boolean async) {
    this.async = async;
  }
}


