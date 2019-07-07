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

package com.uber.athena.flux.converter.impl.testutils.converter;

import com.uber.athena.flux.converter.api.converter.ConverterContext;
import com.uber.athena.flux.converter.api.converter.ConverterRuleOpt;
import com.uber.athena.flux.converter.api.node.Node;
import com.uber.athena.flux.converter.api.node.dsl.DslNode;
import com.uber.athena.flux.converter.api.node.element.ElementNode;
import com.uber.athena.flux.converter.api.node.expression.ExpressionNode;
import com.uber.athena.flux.converter.api.rule.ConverterRule;
import com.uber.athena.flux.converter.api.rule.ConverterRuleSet;
import com.uber.athena.flux.converter.api.rule.RuleSet;
import com.uber.athena.flux.converter.api.traverser.TraverserContext;
import com.uber.athena.flux.converter.api.traverser.TraverserOpt;
import com.uber.athena.flux.converter.impl.converter.RuleSetConverter;
import com.uber.athena.flux.converter.impl.testutils.node.dsl.BaseDslNode;

public class SimpleConverter extends RuleSetConverter {

  public SimpleConverter(RuleSet<ConverterRule> ruleSet) {
    this.converterRuleSet = ruleSet;
  }

  public SimpleConverter(ConverterRule... rules) {
    this.converterRuleSet = ConverterRuleSet.ofList(rules);
  }

  /**
   * Basic ordered-exhaust conversion.
   *
   * <p>This converter applies rules in ordered until no more rules can match
   * for a single node before stopping. It then return back to traverser.
   *
   * @param traverserOpt traverser operator object to be converted.
   * @param traverserContext traversing context.
   * @param converterContext converter context.
   */
  @Override
  public void convert(
      TraverserOpt traverserOpt,
      TraverserContext traverserContext,
      ConverterContext converterContext) {
    for (ConverterRule rule : converterRuleSet) {
      ConverterRuleOpt ruleOpt = new ConverterRuleOpt(
          traverserOpt.getVertexId(),
          getNode(
              traverserOpt.getVertexId(),
              traverserContext,
              converterContext,
              rule.getInputClass()),
          rule,
          traverserOpt.getUpstreams()
      );
      if (rule.matches(ruleOpt, traverserContext, converterContext)) {
        rule.onMatch(ruleOpt, traverserContext, converterContext);
      }
    }
  }

  /**
   * Check if the converted DAG is valid.
   *
   * @param traverserOpt traverser operator object to be converted.
   * @param traverserContext traversing context.
   * @param converterContext converter context.
   */
  @Override
  public void validate(
      TraverserOpt traverserOpt,
      TraverserContext traverserContext,
      ConverterContext converterContext) {
    // TODO(@walterddr) add this.
  }

  /**
   * Utility to get proper node from the converter context.
   */
  @SuppressWarnings("unchecked")
  private static <T extends Node> T getNode(
      String vertexId,
      TraverserContext traverserContext,
      ConverterContext converterContext,
      Class<?> clazz
  ) {
    T node = null;
    if (DslNode.class.isAssignableFrom(clazz)) {
      node = (T) converterContext.getDslNode(vertexId);
    } else if (ElementNode.class.isAssignableFrom(clazz)) {
      node = (T) converterContext.getElementNode(vertexId);
    } else if (ExpressionNode.class.isAssignableFrom(clazz)) {
      node = (T) converterContext.getExpressionNode(vertexId);
    } else {
      throw new IllegalArgumentException("Cannot find correct node within context! "
          +  "vertexId & required class not found: " + vertexId + " | " + clazz);
    }
    // For all non-existing nodes, we start with Base Dsl Node for this converter.
    if (node == null) {
      return (T) new BaseDslNode(
          vertexId,
          traverserContext.getTraverserOpt(vertexId).getVertexDef()
      );
    } else {
      return node;
    }
  }
}
