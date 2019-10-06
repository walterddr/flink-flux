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
 * A set of {@code Rule}s that works together to construct a plan.
 *
 * <p>the set of rule is usually feed to a {@link RuleExecutor}.
 */
public interface RuleSet extends Iterable<Rule> {

  /**
   * The rule matching order for this set of rules.
   *
   * @return the {@link RuleMatchOrder} value.
   */
  default RuleMatchOrder getRuleMatchOrder() {
    return RuleMatchOrder.SEQUENTIAL;
  }

  /**
   * The set of ordering mechanisms supported.
   */
  enum RuleMatchOrder {

    /**
     * Match in arbitrary order.
     *
     * <p>This is the default because it is efficient, and most rules don't care
     * about order.
     */
    ARBITRARY,

    /**
     * Match from top down.
     *
     * <p>A match attempts to apply rules in a {@link RuleSet} sequentially.
     */
    SEQUENTIAL
  }
}
