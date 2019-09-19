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

/**
 * Defines a converter rule that transforms nodes from one type to another.
 */
public interface Rule {

  /**
   * Check a rule match and perform actions defined in the {@link RuleCall}.
   *
   * <p>At the time that this method is called, {@link RuleCall} holds
   * the rule firing context.
   *
   * <p>Typically a ruleCall would include a {@link Rule}, the node/vertex
   * that's associate with the rule, and necessary upstream/downstream
   * context.
   *
   * @param ruleCall the rule invocation context.
   */
  void onMatch(RuleCall ruleCall);
}
