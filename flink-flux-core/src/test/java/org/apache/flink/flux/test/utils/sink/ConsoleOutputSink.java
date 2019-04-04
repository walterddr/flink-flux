package org.apache.flink.flux.test.utils.sink;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class ConsoleOutputSink implements SinkFunction<String> {

  public ConsoleOutputSink() {

  }

  @Override
  public void invoke(String value, SinkFunction.Context context) throws Exception {
    System.out.println(value);
  }
}
