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
 */

package com.uber.athena.flux.converter.api.converter;

import com.uber.athena.flux.converter.api.node.Node;
import com.uber.athena.flux.converter.api.rule.Rule;
import com.uber.athena.flux.converter.api.rule.RuleOpt;
import com.uber.athena.flux.model.StreamDef;

import java.util.List;
import java.util.Map;

/**
 * Base converter operand representing relationships of a node being converted.
 */
public class ConverterRuleOpt implements RuleOpt {

  protected final String vertexId;
  protected final Node node;
  protected final Rule rule;
  protected final Map<String, Node> upstreamNodeMap;
  protected final List<StreamDef> upstreamList;

  public ConverterRuleOpt(
      String vertexId,
      Node node,
      Rule rule,
      Map<String, Node> upstreamNodeMap,
      List<StreamDef> upstreamList) {
    this.vertexId = vertexId;
    this.node = node;
    this.rule = rule;
    this.upstreamNodeMap = upstreamNodeMap;
    this.upstreamList = upstreamList;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Node> T getNode() {
    return (T) node;
  }

  @Override
  public Rule getRule() {
    return rule;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R extends Node> R getUpstreamNode(String vertexId) {
    return (R) upstreamNodeMap.get(vertexId);
  }

  @Override
  public List<StreamDef> getUpstreams() {
    return upstreamList;
  }

  @Override
  public List<StreamDef> getDownstreams() {
    throw new UnsupportedOperationException("Converter does not support on downstream.");
  }
}
