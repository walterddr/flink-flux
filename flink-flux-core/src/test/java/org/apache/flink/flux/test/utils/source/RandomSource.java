package org.apache.flink.flux.test.utils.source;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

public class RandomSource implements SourceFunction<String> {

  private final int limit;

  public RandomSource(int limit) {
    this.limit = limit;
  }

  public RandomSource() {
    this(-1);
  }

  @Override
  public void run(SourceContext<String> sourceContext) throws Exception {
    int count = 0;
    while (limit == -1 || count < limit) {
      Thread.sleep(100L);
      sourceContext.collect(nextTuple());
      count++;
    }
  }

  @Override
  public void cancel() {
    // ...
  }

  private String nextTuple() {
    String[] words = new String[]{"nathan", "mike", "jackson", "golda", "bertels"};
    Random rand = new Random();
    return words[rand.nextInt(words.length)];
  }
}
