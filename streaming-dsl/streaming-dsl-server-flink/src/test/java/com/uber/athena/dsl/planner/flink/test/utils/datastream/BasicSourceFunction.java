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

package com.uber.athena.dsl.planner.flink.test.utils.datastream;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * Basic test source function.
 */
public class BasicSourceFunction implements SourceFunction<String> {

  private final int repeat;
  private final long intervalMs;

  public BasicSourceFunction(int repeat) {
    this(repeat, 10);
  }

  public BasicSourceFunction(int repeat, long intervalMs) {
    this.repeat = repeat;
    this.intervalMs = intervalMs;
  }

  @Override
  public void run(SourceContext<String> sourceContext) throws Exception {
    for (int i = 0; i < repeat; i++) {
      sourceContext.collect("Hello World!");
      Thread.sleep(intervalMs);
    }
  }

  @Override
  public void cancel() {
  }
}
