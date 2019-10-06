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

import java.util.HashMap;
import java.util.Map;

/**
 * A base RuleCall implementation.
 *
 * <p>Base implementation only keeps one Rule, the {@link ElementNode} before
 * the construction invocation, and a Map of {@link StreamDef} and
 * {@link RelationNode}s associated with this vertex.
 */
public class RuleCallImpl implements RuleCall {

  private final String vertexId;
  private final Rule rule;
  private final ElementNode element;
  private final Map<String, StreamDef> upstreamDefs;
  private final Map<String, RelationNode> upstreamNodes;

  private RelationNode relationNode;
  private boolean isRelationFinal;

  public RuleCallImpl(
      String vertexId,
      Rule rule,
      ElementNode element) {
    this(vertexId, rule, element, new HashMap<>(), new HashMap<>());
  }

  public RuleCallImpl(
      String vertexId,
      Rule rule,
      ElementNode element,
      Map<String, StreamDef> upstreamDefs,
      Map<String, RelationNode> upstreamNodes) {
    this.vertexId = vertexId;
    this.rule = rule;
    this.element = element;
    this.upstreamDefs = upstreamDefs;
    this.upstreamNodes = upstreamNodes;
    this.isRelationFinal = false;
  }

  @Override
  public String getVertexId() {
    return vertexId;
  }

  @Override
  public Rule getRule() {
    return rule;
  }

  @Override
  public ElementNode getElementNode() {
    return element;
  }

  @Override
  public Map<String, StreamDef> getUpstreamDefMapping() {
    return upstreamDefs;
  }

  @Override
  public Map<String, RelationNode> getUpstreamRelationMapping() {
    return upstreamNodes;
  }

  @Override
  public RelationNode getRelationNode() {
    return relationNode;
  }

  @Override
  public boolean isRelationFinal() {
    return isRelationFinal;
  }

  @Override
  public void setRelationNode(RelationNode relationNode) {
    this.isRelationFinal = true;
    this.relationNode = relationNode;
  }
}
