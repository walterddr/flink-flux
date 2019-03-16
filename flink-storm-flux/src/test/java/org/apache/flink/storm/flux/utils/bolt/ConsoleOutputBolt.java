package org.apache.flink.storm.flux.utils.bolt;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

public class ConsoleOutputBolt extends BaseBasicBolt {

  public ConsoleOutputBolt() {
  }

  public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
    System.out.println(tuple);
  }

  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
  }
}
