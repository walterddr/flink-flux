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

package com.uber.athena.flux.flink.test.utils.operator;

import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.api.operators.co.CoProcessOperator;
import org.apache.flink.util.Collector;

public class BasicCoStreamMergeOperator extends CoProcessOperator<String, String, String> {

  public BasicCoStreamMergeOperator() {
    this(PassThroughCoProcessFunction.getInstance());
  }

  public BasicCoStreamMergeOperator(CoProcessFunction<String, String, String> flatMapper) {
    super(flatMapper);
  }

  private static class PassThroughCoProcessFunction extends CoProcessFunction<String, String, String> {

    public static PassThroughCoProcessFunction getInstance() {
      return new PassThroughCoProcessFunction();
    }

    @Override
    public void processElement1(String s, Context context, Collector<String> collector) throws Exception {
      collector.collect(s);
    }

    @Override
    public void processElement2(String s, Context context, Collector<String> collector) throws Exception {
      collector.collect(s);
    }
  }
}
