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

import com.uber.athena.dsl.planner.flink.relation.rules.FlinkRuleSet;
import com.uber.athena.dsl.planner.flink.relation.rules.datastream.base.DataStreamFunctionRule;
import com.uber.athena.dsl.planner.flink.relation.rules.datastream.co.DataStreamCoFunctionRule;
import com.uber.athena.dsl.planner.flink.relation.rules.datastream.sink.SinkFunctionRule;
import com.uber.athena.dsl.planner.flink.relation.rules.datastream.source.SourceFunctionRule;
import com.uber.athena.dsl.planner.relation.rule.Rule;
import org.apache.flink.annotation.VisibleForTesting;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Default {@link Rule}s for building a datastream-based application.
 */
public class FlinkDataStreamRuleSet extends FlinkRuleSet {

  private static final List<Rule> STANDARD_DATASTREAM_RULE_SET = Arrays.asList(
      new SinkFunctionRule(),
      new SourceFunctionRule(),
      new DataStreamFunctionRule(),
      new DataStreamCoFunctionRule()
  );

  public FlinkDataStreamRuleSet(int parallelism, Configuration flinkConf) {
    this(parallelism,
        flinkConf,
        StreamExecutionEnvironment.createLocalEnvironment(parallelism, flinkConf));
  }

  @VisibleForTesting
  public FlinkDataStreamRuleSet(
      int parallelism,
      Configuration flinkConf,
      StreamExecutionEnvironment sEnv) {
    super(parallelism, flinkConf, sEnv);
    BaseDataStreamRule.setStreamEnv(super.sEnv);
  }

  @Override
  public Iterator<Rule> iterator() {
    return STANDARD_DATASTREAM_RULE_SET.iterator();
  }
}
