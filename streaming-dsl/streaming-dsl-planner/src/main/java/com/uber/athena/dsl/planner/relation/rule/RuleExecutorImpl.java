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

import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.model.StreamDef;
import com.uber.athena.dsl.planner.model.VertexNode;
import com.uber.athena.dsl.planner.relation.RelationNode;
import com.uber.athena.dsl.planner.utils.ConstructionException;
import com.uber.athena.dsl.planner.utils.RuleCallException;

import java.util.Map;

/**
 * Base implementation of a RuleExecutor.
 *
 * <p>Concrete implementation of an executor should depend on a concrete
 * implementation of {@link RuleCall}.
 */
public class RuleExecutorImpl implements RuleExecutor {

  private final RuleSet ruleSet;
  private final Map<String, Object> config;

  public RuleExecutorImpl(
      RuleSet ruleSet,
      Map<String, Object> config
  ) {
    this.ruleSet = ruleSet;
    this.config = config;
  }

  /**
   * Executes a {@link RuleSet} on a specific {@link VertexNode}.
   */
  @Override
  public RelationNode convert(
      VertexNode vertex,
      ElementNode element,
      Map<String, StreamDef> streamDefs,
      Map<String, RelationNode> relations) throws ConstructionException {

    switch (ruleSet.getRuleMatchOrder()) {
      case SEQUENTIAL:
        return convertSequential(vertex, element, streamDefs, relations);
      default:
        throw new RuleCallException("Unsupported ruleSet execution order:"
            + ruleSet.getRuleMatchOrder());
    }
  }

  public RuleSet getRuleSet() {
    return ruleSet;
  }

  public Map<String, Object> getConfig() {
    return config;
  }

  /**
   * Converts a relation node by applying the {@link RuleSet} sequentially.
   *
   * @param vertex the vertex node.
   * @param element the constructed element.
   * @throws RuleCallException if convert fails.
   */
  private RelationNode convertSequential(
      VertexNode vertex,
      ElementNode element,
      Map<String, StreamDef> streamDefs,
      Map<String, RelationNode> relations) throws ConstructionException {

    for (Rule rule : ruleSet) {
      RuleCall ruleCall =
          new RuleCallImpl(vertex.getVertexId(), rule, element, streamDefs, relations);

      // Check rule match
      if (rule.matches(ruleCall)) {
        // invoke rule firing
        RuleCall newRuleCall = rule.onMatch(ruleCall);
        // if rule constructs final relation returns the result.
        if (newRuleCall.isRelationFinal()) {
          return newRuleCall.getRelationNode();
        }
      }
    }
    throw new ConstructionException("Unable to construct relation for: " + vertex.getVertexId());
  }
}
