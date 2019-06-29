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

package com.uber.athena.flux.converter.api.rule;

import com.uber.athena.flux.converter.api.converter.ConverterContext;
import com.uber.athena.flux.converter.api.node.Node;
import com.uber.athena.flux.converter.api.traverser.TraverserContext;

import java.util.Objects;

public abstract class ConverterRule<IN extends Node, OUT extends Node> implements Rule {

  protected final String description;
  protected final Class<IN> inTrait;
  protected final Class<OUT> outTrait;

  private TraverserContext traverserContext;
  private ConverterContext converterContext;

  public ConverterRule(
      Class<IN> in,
      Class<OUT> out,
      String description) {
    this.description = description;
    this.inTrait = Objects.requireNonNull(in);
    this.outTrait = Objects.requireNonNull(out);
  }

  /**
   * Action to be invoked when a specific {@link Node} matches the firing criteria.
   *
   * <p>This is usually the preprocessing mechanism to prepare the input node
   * for conversion.
   *
   * @param node node object.
   * @param traverserContext traverser context
   * @param converterContext converter context
   */
  public abstract void onMatch(
      IN node,
      TraverserContext traverserContext,
      ConverterContext converterContext);

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Node> void onMatch(T node) {
    Class nodeClass = node.getClass();
    if (inTrait.isInstance(nodeClass)) {
      onMatch((IN) node, traverserContext, converterContext);
      OUT convertedNode = convertNode((IN) node, traverserContext, converterContext);
      converterContext.processConvertedResult(
          convertedNode,
          convertedNode.getClass()
      );
    } else {
      throw new UnsupportedOperationException("Illegal node conversion! converter accepts: "
          + inTrait + " while node type: " + nodeClass);
    }
  }

  /**
   * Deteremine whether a specific {@link Node} matches the firing criteria.
   *
   * @param node node object.
   * @param traverserContext traverser context
   * @param converterContext converter context
   * @return true if this rule can be fired with the node setup.
   */
  public abstract boolean matches(
      IN node,
      TraverserContext traverserContext,
      ConverterContext converterContext);

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Node> boolean matches(T node) {
    Class nodeClass = node.getClass();
    if (inTrait.equals(nodeClass)) {
      return matches((IN) node, traverserContext, converterContext);
    } else {
      throw new UnsupportedOperationException("Illegal node conversion! converter accepts: "
          + inTrait + " while node type: " + nodeClass);
    }
  }

  public void setTraverserContext(TraverserContext context) {
    this.traverserContext = context;
  }

  public void setConverterContext(ConverterContext context) {
    this.converterContext = context;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Convert the node into the desired result node type.
   *
   * @param node input node.
   * @param traverserContext traverser context
   * @param converterContext converter context
   * @return converted node.
   */
  public abstract OUT convertNode(
      IN node,
      TraverserContext traverserContext,
      ConverterContext converterContext);
}
