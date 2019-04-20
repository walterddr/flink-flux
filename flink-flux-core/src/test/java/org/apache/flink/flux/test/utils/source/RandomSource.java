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
