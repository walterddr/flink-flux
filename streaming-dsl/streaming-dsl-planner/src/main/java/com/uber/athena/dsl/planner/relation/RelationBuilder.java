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

package com.uber.athena.dsl.planner.relation;

import com.uber.athena.dsl.planner.Blackboard;
import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.relation.rule.RuleExecutor;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Builder class for constructing a {@link RelationNode} object.
 */
public class RelationBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(RelationBuilder.class);

  /**
   * Configuration properties for {@link Rule}s and {@link TypeFactory}.
   */
  private Properties config;

  /**
   * Type/Schema factory used to determine output schema of an Element.
   */
  private TypeFactory typeFactory;

  /**
   * Executor of {@link Rule}s.
   *
   * <p>{@link Rule}s are encapsulated within the executor environment.
   */
  private RuleExecutor ruleExecutor;

  /**
   * Construct a relation builder.
   *
   * @param builderConfig configuration properties.
   * @param typeFactory the type factory to construct relation produce type.
   * @param ruleExecutor executor of rules / ruleset.
   */
  public RelationBuilder(
      Properties builderConfig,
      TypeFactory typeFactory,
      RuleExecutor ruleExecutor) {
    this.config = builderConfig;
    this.typeFactory = typeFactory;
    this.ruleExecutor = ruleExecutor;
  }

  /**
   * Construct the relation for this topology for every vertex.
   *
   * <p>Constructed nodes will be put into the relation {@link Blackboard}
   * provided. this blackboard will be access multiple times.
   *
   * @param topology topology
   * @param elementBlackboard blackboard for elements.
   * @param relationBlackboard blackboard for relations.
   */
  public void construct(
      Topology topology,
      Blackboard<ElementNode> elementBlackboard,
      Blackboard<RelationNode> relationBlackboard) {
    // ...
  }
}
