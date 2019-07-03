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

package com.uber.athena.flux.converter.impl.testutils.rule;

import com.uber.athena.flux.converter.api.converter.ConverterContext;
import com.uber.athena.flux.converter.api.node.Node;
import com.uber.athena.flux.converter.api.node.element.ElementNode;
import com.uber.athena.flux.converter.api.rule.ConverterRule;
import com.uber.athena.flux.converter.api.rule.RuleOpt;
import com.uber.athena.flux.converter.api.traverser.TraverserContext;
import com.uber.athena.flux.converter.impl.testutils.expression.Expression;
import com.uber.athena.flux.converter.impl.testutils.expression.TraverseTreeExpression;
import com.uber.athena.flux.converter.impl.testutils.node.expression.TraverseTreeExpressionNode;
import com.uber.athena.flux.converter.impl.testutils.operator.Operator;
import com.uber.athena.flux.model.StreamDef;

import java.util.HashMap;
import java.util.Map;

public class SimpleElementLinkageRule extends ConverterRule {

  public static final SimpleElementLinkageRule INSTANCE = new SimpleElementLinkageRule(
      ElementNode.class, TraverseTreeExpressionNode.class, "ElementLinkage"
  );

  SimpleElementLinkageRule(
      Class<ElementNode> in,
      Class<TraverseTreeExpressionNode> out,
      String description) {
    super(in, out, description);
  }

  @Override
  public boolean matches(
      RuleOpt ruleOpt,
      TraverserContext traverserContext,
      ConverterContext converterContext) {
    // ready to convert if all upstreams has expressions generated.
    for (StreamDef stream : ruleOpt.getUpstreams()) {
      String upstreamVertex = stream.getFromVertex();
      if (converterContext.getExpressionNode(upstreamVertex) == null) {
        return false;
      }
    }
    return super.matches(ruleOpt, traverserContext, converterContext);
  }

  @Override
  public void onMatch(
      RuleOpt ruleOpt,
      TraverserContext traverserContext,
      ConverterContext converterContext) {
    TraverseTreeExpressionNode node = new TraverseTreeExpressionNode(
        ruleOpt.getNode().getVertexId(),
        ruleOpt.getNode().getVertexDef());
    Map<String, Expression> upstreamMap = new HashMap<>();
    for (StreamDef stream : ruleOpt.getUpstreams()) {
      String upstreamVertex = stream.getFromVertex();
      upstreamMap.put(upstreamVertex,
          (TraverseTreeExpression) converterContext.getExpressionNode(
              upstreamVertex).getExpression());
    }
    node.setExpression(new TraverseTreeExpression(
        ruleOpt.getNode().getVertexId(),
        ruleOpt.getNode().getVertexDef(),
        (Operator) ((ElementNode) ruleOpt.getNode()).getElement(),
        upstreamMap
    ));
    converterContext.processConvertedResult(node, TraverseTreeExpressionNode.class);
  }
}
