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

package com.uber.athena.dsl.planner.relation.rule;

import com.uber.athena.dsl.planner.relation.RelationNode;

/**
 * A RuleCall encapsulates a rule and its caller context.
 */
public interface RuleCall {

  /**
   * Returns the current vertex node that matched by this rule.
   *
   * @return the node itself
   */
  <T extends RelationNode> T getNode();

  /**
   * Returns rule desired to be invoked.
   *
   * @return the rule itself
   */
  Rule getRule();
}
