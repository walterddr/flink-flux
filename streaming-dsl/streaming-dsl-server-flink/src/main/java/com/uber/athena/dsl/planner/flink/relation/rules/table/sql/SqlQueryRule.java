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

package com.uber.athena.dsl.planner.flink.relation.rules.table.sql;

import com.uber.athena.dsl.planner.flink.relation.rules.table.BaseFlinkTableRule;
import com.uber.athena.dsl.planner.relation.rule.RuleCall;

/**
 * Connect a Stringified SQL query to a table environment.
 */
public class SqlQueryRule extends BaseFlinkTableRule {

  @Override
  public RuleCall onMatch(RuleCall ruleCall) {
    return null;
  }

  @Override
  public boolean matches(RuleCall ruleCall) {
    return false;
  }
}
