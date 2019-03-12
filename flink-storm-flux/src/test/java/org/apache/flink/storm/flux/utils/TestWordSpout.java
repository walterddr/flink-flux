package org.apache.flink.storm.flux.utils;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TestWordSpout extends BaseRichSpout {
  public static Logger LOG = LoggerFactory.getLogger(org.apache.storm.testing.TestWordSpout.class);
  boolean _isDistributed;
  SpoutOutputCollector _collector;

  public TestWordSpout() {
    this(true);
  }

  public TestWordSpout(boolean isDistributed) {
    this._isDistributed = isDistributed;
  }

  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    this._collector = collector;
  }

  public void close() {
  }

  public void nextTuple() {
    Utils.sleep(100L);
    String[] words = new String[]{"nathan", "mike", "jackson", "golda", "bertels"};
    Random rand = new Random();
    String word = words[rand.nextInt(words.length)];
    this._collector.emit(new Values(new Object[]{word}));
  }

  public void ack(Object msgId) {
  }

  public void fail(Object msgId) {
  }

  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields(new String[]{"word"}));
  }

  public Map<String, Object> getComponentConfiguration() {
    if (!this._isDistributed) {
      Map<String, Object> ret = new HashMap();
      ret.put("topology.max.task.parallelism", 1);
      return ret;
    } else {
      return null;
    }
  }
}
