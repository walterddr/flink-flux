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
import com.uber.athena.dsl.planner.relation.RelationNode;

import java.util.Map;

/**
 * A RuleCall is a context for invoking a {@link Rule}.
 */
public interface RuleCall {

  /**
   * Returns the vertex ID in the original DSL topology.
   *
   * @return the vertex ID.
   */
  String getVertexId();

  /**
   * Returns rule desired to be invoked.
   *
   * @return the rule itself
   */
  Rule getRule();

  /**
   * Returns the current {@link ElementNode}.
   *
   * @return the node itself
   */
  ElementNode getElementNode();

  /**
   * Get all the connected upstream definitions.
   *
   * @return a mapping of upstream vertex Id(s) and its associated stream definition.
   */
  Map<String, StreamDef> getUpstreamDefMapping();

  /**
   * Get all the connected upstream relation nodes.
   *
   * @return a mapping of upstream vertex Id(s) and its associated relation nodes.
   */
  Map<String, RelationNode> getUpstreamRelationMapping();

  /**
   * Determines whether the relation node is constructed.
   *
   * <p>the relation node can be constructed by {@link Rule#onMatch(RuleCall)}
   * method invocation. It is not necessary for the onMatch method to
   * construct a relation.
   *
   * @return true if the relation node is constructed.
   */
  boolean isRelationFinal();

  /**
   * Returns the constructed {@link RelationNode}, null if not yet constructed.
   *
   * @return the constructed relation node.
   */
  RelationNode getRelationNode();

  /**
   * Sets the constructee relationNode to the ruleCall object.
   *
   * @param relationNode the relation node constructed.
   */
  void setRelationNode(RelationNode relationNode);
}
