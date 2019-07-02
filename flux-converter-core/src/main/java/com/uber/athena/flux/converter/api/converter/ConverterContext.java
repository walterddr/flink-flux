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
import com.uber.athena.flux.converter.api.node.dsl.DslNode;
import com.uber.athena.flux.converter.api.node.element.ElementNode;
import com.uber.athena.flux.converter.api.node.expression.ExpressionNode;

/**
 * This object saves the intermediate storage context for a converter.
 *
 * <p>Intermediate results are stored for references and later invocations.
 */
public interface ConverterContext {

  /**
   * Processed converted node objects and make it available in the context.
   *
   * @param convertNode converted node
   * @param nodeClazz class of the converted node
   */
  void processConvertedResult(Node convertNode, Class<? extends Node> nodeClazz);

  DslNode getDslNode(String vertexId);

  ElementNode getElementNode(String vertexId);

  ExpressionNode getExpressionNode(String vertexId);
}
