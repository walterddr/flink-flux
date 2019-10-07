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

package com.uber.athena.dsl.planner.flink.relation.rules.table;

import com.uber.athena.dsl.planner.flink.relation.rules.FlinkRuleSet;
import com.uber.athena.dsl.planner.flink.relation.rules.table.sink.TableSinkRule;
import com.uber.athena.dsl.planner.flink.relation.rules.table.source.TableSourceRule;
import com.uber.athena.dsl.planner.flink.relation.rules.table.sql.SqlQueryRule;
import com.uber.athena.dsl.planner.relation.rule.Rule;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Default {@link Rule}s for building a table-based application.
 */
public class FlinkTableRuleSet extends FlinkRuleSet {

  private static final List<Rule> STANDARD_TABLE_RULE_SET = Arrays.asList(
      new TableSinkRule(),
      new TableSourceRule(),
      new SqlQueryRule()
  );

  public FlinkTableRuleSet(
      int parallelism,
      Configuration flinkConf) {
    this(parallelism,
        flinkConf,
        StreamExecutionEnvironment.createLocalEnvironment(parallelism, flinkConf));
  }

  public FlinkTableRuleSet(
      int parallelism,
      Configuration flinkConf,
      StreamExecutionEnvironment sEnv) {
    super(parallelism, flinkConf, sEnv);
    BaseFlinkTableRule.setStreamExecEnv(super.sEnv);
    BaseFlinkTableRule.setTableEnv(StreamTableEnvironment.create(sEnv));
  }

  @Override
  public Iterator<Rule> iterator() {
    return STANDARD_TABLE_RULE_SET.iterator();
  }
}
