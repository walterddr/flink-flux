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

package org.apache.flink.flux.test.utils.operator;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.operators.ProcessOperator;
import org.apache.flink.util.Collector;

public class BasicPassThroughOperator extends ProcessOperator<String, String> {

  public BasicPassThroughOperator(ProcessFunction<String, String> function) {
    super(function);
  }

  public BasicPassThroughOperator() {
    this(PassThroughFunction.getInstance());
  }

  private static class PassThroughFunction extends ProcessFunction<String, String> {

    public static PassThroughFunction getInstance() {
      return new PassThroughFunction();
    }

    @Override
    public void processElement(String s, Context context, Collector<String> collector) throws Exception {
      collector.collect(s);
    }
  }
}
