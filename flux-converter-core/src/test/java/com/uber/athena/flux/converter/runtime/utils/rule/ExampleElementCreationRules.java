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

package com.uber.athena.flux.converter.runtime.utils.rule;

import com.uber.athena.flux.converter.api.converter.ConverterContext;
import com.uber.athena.flux.converter.api.node.dsl.DslNode;
import com.uber.athena.flux.converter.api.rule.ConverterRule;
import com.uber.athena.flux.converter.api.traverser.TraverserContext;
import com.uber.athena.flux.converter.runtime.utils.ReflectiveInvokeUtils;
import com.uber.athena.flux.converter.runtime.utils.node.element.BaseElementNode;
import com.uber.athena.flux.converter.runtime.utils.node.element.OneInputElementNode;
import com.uber.athena.flux.converter.runtime.utils.node.element.SourceElementNode;
import com.uber.athena.flux.converter.runtime.utils.node.element.TwoInputElementNode;
import com.uber.athena.flux.model.VertexDef;

public abstract class ExampleElementCreationRules<T extends BaseElementNode> extends ConverterRule<DslNode, T> {

  public ExampleElementCreationRules(Class<DslNode> in, Class<T> out, String description) {
    super(in, out, description);
  }

  @Override
  public void onMatch(
      DslNode node,
      TraverserContext traverserContext,
      ConverterContext converterContext) {
    // no pre-processing needed.
  }

  @Override
  public boolean matches(
      DslNode node,
      TraverserContext traverserContext,
      ConverterContext converterContext) {
    return node.getDownstreamVertexIds().size() == getDesiredUpstreamSize();
  }

  public abstract int getDesiredUpstreamSize();

  public static class SourceCreationRule extends ExampleElementCreationRules<SourceElementNode> {

    public SourceCreationRule(
        Class<DslNode> in,
        Class<SourceElementNode> out,
        String description) {
      super(in, out, description);
    }

    @Override
    public int getDesiredUpstreamSize() {
      return 0;
    }

    @Override
    public SourceElementNode convertNode(
        DslNode node,
        TraverserContext traverserContext,
        ConverterContext converterContext) {
      return constructElement(new SourceElementNode(node.getVertexId(), node.getVertexDef()),
          traverserContext, converterContext);
    }
  }

  public static class OneInputCreationRule extends ExampleElementCreationRules<OneInputElementNode> {

    public OneInputCreationRule(
        Class<DslNode> in,
        Class<OneInputElementNode> out,
        String description) {
      super(in, out, description);
    }

    @Override
    public int getDesiredUpstreamSize() {
      return 1;
    }

    @Override
    public OneInputElementNode convertNode(
        DslNode node,
        TraverserContext traverserContext,
        ConverterContext converterContext) {
      return constructElement(new OneInputElementNode(node.getVertexId(), node.getVertexDef()),
          traverserContext, converterContext);
    }
  }

  public static class TwoInputCreationRule extends ExampleElementCreationRules<TwoInputElementNode> {

    public TwoInputCreationRule(
        Class<DslNode> in,
        Class<TwoInputElementNode> out,
        String description) {
      super(in, out, description);
    }

    @Override
    public int getDesiredUpstreamSize() {
      return 2;
    }

    @Override
    public TwoInputElementNode convertNode(
        DslNode node,
        TraverserContext traverserContext,
        ConverterContext converterContext) {
      return constructElement(new TwoInputElementNode(node.getVertexId(), node.getVertexDef()),
          traverserContext, converterContext);
    }
  }

  private static <R extends BaseElementNode> R constructElement(
      R elementNode,
      TraverserContext traverserContext,
      ConverterContext converterContext) {
    try {
      VertexDef vertexDef = elementNode.getVertexDef();
      Object object = ReflectiveInvokeUtils.buildObject(
          vertexDef, traverserContext, converterContext);
      elementNode.setElement(object);
    } catch (Exception e) {
      throw new RuntimeException("Cannot construct build object!", e);
    }
    return elementNode;
  }
}
