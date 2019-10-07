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

package com.uber.athena.dsl.planner.flink.relation.rules.datastream;

import com.uber.athena.dsl.planner.relation.rule.Rule;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

/**
 * Base class of all datastream API based relation construct rules.
 */
public abstract class BaseDataStreamRule implements Rule, FlinkDataStreamRuleBase {

  private static Map<String, Object> config;
  private static StreamExecutionEnvironment sEnv;

  public static void setConfig(Map<String, Object> config) {
    BaseDataStreamRule.config = config;
  }

  public static void setStreamEnv(StreamExecutionEnvironment sEnv) {
    BaseDataStreamRule.sEnv = sEnv;
  }

  @Override
  public StreamExecutionEnvironment getStreamEnv() {
    return sEnv;
  }

  public static Map<String, Object> getConfig() {
    return config;
  }
}
