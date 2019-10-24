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

package com.uber.athena.dsl.planner.flink.relation.rules;

import com.uber.athena.dsl.planner.flink.relation.rules.datastream.BaseDataStreamRule;
import com.uber.athena.dsl.planner.relation.rule.RuleSet;
import org.apache.flink.annotation.VisibleForTesting;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Base class for any Flink {@link RuleSet}.
 *
 * <p>RuleSet consists of basic operations to configure the runtime environment
 * for {@link com.uber.athena.dsl.planner.relation.rule.RuleExecutor} to
 * construct relation objects.
 */
public abstract class FlinkRuleSet implements RuleSet {
  protected final StreamExecutionEnvironment sEnv;
  protected final Configuration flinkConf;
  protected final int parallelism;

  protected FlinkRuleSet(
      int parallelism,
      Configuration flinkConf) {
    this(parallelism,
        flinkConf,
        StreamExecutionEnvironment.createLocalEnvironment(parallelism, flinkConf));
    BaseDataStreamRule.setStreamEnv(sEnv);
  }

  @VisibleForTesting
  public FlinkRuleSet(
      int parallelism,
      Configuration flinkConf,
      StreamExecutionEnvironment sEnv) {
    this.sEnv = sEnv;
    this.parallelism = parallelism;
    this.flinkConf = flinkConf;
  }

  public StreamExecutionEnvironment getStreamExecutionEnvironment() {
    return sEnv;
  }
}
