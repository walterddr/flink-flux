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

package com.uber.athena.flux.converter.runtime.utils.converter;

import com.uber.athena.flux.converter.api.node.Node;
import com.uber.athena.flux.converter.api.node.dsl.DslNode;
import com.uber.athena.flux.converter.api.node.element.ElementNode;
import com.uber.athena.flux.converter.api.node.expression.ExpressionNode;
import com.uber.athena.flux.converter.runtime.converter.RuleSetConverterContext;

import java.util.HashMap;
import java.util.Map;

public class SimpleConverterContext extends RuleSetConverterContext<Node> {

  private final Map<String, DslNode> dslNodeMap = new HashMap<>();
  private final Map<String, ElementNode> elementNodeMap = new HashMap<>();
  private final Map<String, ExpressionNode> expressionNodeMap = new HashMap<>();

  public SimpleConverterContext() {
  }

  /**
   * Example context to process converted results.
   *
   * <p>Save all nodes in each categories (DSL, ELEMENT, and EXPRESSION)
   * Only the latest of each categories is considered as the conversion result.
   *
   * @param convertNode converted node object.
   * @param nodeClazz node clazz
   */
  @Override
  public void processConvertedResult(Node convertNode, Class<? extends Node> nodeClazz) {
    convertNode.computeDigest();
    if (convertNode instanceof DslNode) {
      dslNodeMap.put(convertNode.getVertexId(), (DslNode) convertNode);
    } else if (convertNode instanceof ElementNode) {
      elementNodeMap.put(convertNode.getVertexId(), (ElementNode) convertNode);
    } else if (convertNode instanceof ExpressionNode) {
      expressionNodeMap.put(convertNode.getVertexId(), (ExpressionNode) convertNode);
    }
  }

  @Override
  public DslNode getDslNode(String vertexId) {
    return dslNodeMap.get(vertexId);
  }

  @Override
  public ElementNode getElementNode(String vertexId) {
    return elementNodeMap.get(vertexId);
  }

  @Override
  public ExpressionNode getExpressionNode(String vertexId) {
    return expressionNodeMap.get(vertexId);
  }
}
