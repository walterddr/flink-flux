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
import com.uber.athena.flux.converter.api.node.dsl.DslNode;
import com.uber.athena.flux.converter.api.rule.ConverterRule;
import com.uber.athena.flux.converter.api.rule.RuleOpt;
import com.uber.athena.flux.converter.api.traverser.TraverserContext;
import com.uber.athena.flux.converter.impl.testutils.node.dsl.OperatorNode;
import com.uber.athena.flux.converter.impl.testutils.node.dsl.SinkNode;
import com.uber.athena.flux.converter.impl.testutils.node.dsl.SourceNode;

public abstract class SimpleDslConversionRules<T extends DslNode> extends ConverterRule {

  SimpleDslConversionRules(Class<DslNode> in, Class<T> out, String description) {
    super(in, out, description);
  }

  public static class SourceConverter extends SimpleDslConversionRules<SourceNode> {

    public static final SourceConverter INSTANCE = new SourceConverter(
        DslNode.class, SourceNode.class, "SourceConverter"
    );

    SourceConverter(Class<DslNode> in, Class<SourceNode> out, String description) {
      super(in, out, description);
    }

    @Override
    public boolean matches(
        RuleOpt ruleOpt,
        TraverserContext traverserContext,
        ConverterContext converterContext) {
      Node node = ruleOpt.getNode();
      String vertexId = node.getVertexDef().getId();
      if (!traverserContext.getTopologyDef().getSources().containsKey(vertexId)) {
        return false;
      }
      return super.matches(ruleOpt, traverserContext, converterContext);
    }

    @Override
    public void onMatch(
        RuleOpt ruleOpt,
        TraverserContext traverserContext,
        ConverterContext converterContext) {
      try {
        Node node = ruleOpt.getNode();
        String vertexId = node.getVertexDef().getId();
        if (traverserContext.getTopologyDef().getSources().containsKey(vertexId)) {
          converterContext.processConvertedResult(
              new SourceNode(node.getVertexId(), node.getVertexDef()),
              SourceNode.class);
        }
      } catch (Exception e) {
        throw new UnsupportedOperationException("cannot convert node!", e);
      }
    }
  }

  public static class SinkConverter extends SimpleDslConversionRules<SinkNode> {

    public static final SinkConverter INSTANCE = new SinkConverter(
        DslNode.class, SinkNode.class, "SinkConverter"
    );

    SinkConverter(Class<DslNode> in, Class<SinkNode> out, String description) {
      super(in, out, description);
    }

    @Override
    public boolean matches(
        RuleOpt ruleOpt,
        TraverserContext traverserContext,
        ConverterContext converterContext) {
      Node node = ruleOpt.getNode();
      String vertexId = node.getVertexDef().getId();
      if (!traverserContext.getTopologyDef().getSinks().containsKey(vertexId)) {
        return false;
      }
      return super.matches(ruleOpt, traverserContext, converterContext);
    }

    @Override
    public void onMatch(
        RuleOpt ruleOpt,
        TraverserContext traverserContext,
        ConverterContext converterContext) {
      try {
        Node node = ruleOpt.getNode();
        String vertexId = node.getVertexDef().getId();
        if (traverserContext.getTopologyDef().getSinks().containsKey(vertexId)) {
          converterContext.processConvertedResult(
              new SinkNode(node.getVertexId(), node.getVertexDef()),
              SinkNode.class);
        }
      } catch (Exception e) {
        throw new UnsupportedOperationException("cannot convert node!", e);
      }
    }
  }

  public static class OperatorConverter extends SimpleDslConversionRules<OperatorNode> {

    public static final OperatorConverter INSTANCE = new OperatorConverter(
        DslNode.class, OperatorNode.class, "OperatorConverter"
    );

    OperatorConverter(Class<DslNode> in, Class<OperatorNode> out, String description) {
      super(in, out, description);
    }

    @Override
    public boolean matches(
        RuleOpt ruleOpt,
        TraverserContext traverserContext,
        ConverterContext converterContext) {
      Node node = ruleOpt.getNode();
      String vertexId = node.getVertexDef().getId();
      if (!traverserContext.getTopologyDef().getOperators().containsKey(vertexId)) {
        return false;
      }
      return super.matches(ruleOpt, traverserContext, converterContext);
    }

    @Override
    public void onMatch(
        RuleOpt ruleOpt,
        TraverserContext traverserContext,
        ConverterContext converterContext) {
      try {
        Node node = ruleOpt.getNode();
        String vertexId = node.getVertexDef().getId();
        if (traverserContext.getTopologyDef().getOperators().containsKey(vertexId)) {
          converterContext.processConvertedResult(
              new OperatorNode(node.getVertexId(), node.getVertexDef()),
              OperatorNode.class);
        }
      } catch (Exception e) {
        throw new UnsupportedOperationException("cannot convert node!", e);
      }
    }
  }
}
