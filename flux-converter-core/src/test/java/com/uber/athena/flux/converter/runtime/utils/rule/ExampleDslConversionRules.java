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
import com.uber.athena.flux.converter.api.node.BaseNode;
import com.uber.athena.flux.converter.api.node.dsl.DslNode;
import com.uber.athena.flux.converter.api.rule.ConverterRule;
import com.uber.athena.flux.converter.api.traverser.TraverserContext;
import com.uber.athena.flux.converter.runtime.node.dsl.OperatorNode;
import com.uber.athena.flux.converter.runtime.node.dsl.SinkNode;
import com.uber.athena.flux.converter.runtime.node.dsl.SourceNode;

public abstract class ExampleDslConversionRules<T extends DslNode>
    extends ConverterRule<DslNode, T> {

  public ExampleDslConversionRules(Class<DslNode> in, Class<T> out, String description) {
    super(in, out, description);
  }

  @Override
  public void onMatch(
      DslNode node,
      TraverserContext traverserContext,
      ConverterContext converterContext) {
    // no pre-processing.
  }

  @Override
  public boolean matches(
      DslNode node,
      TraverserContext traverserContext,
      ConverterContext converterContext) {
    return outTrait.isInstance(node);
  }

  public static class SourceConverter extends ExampleDslConversionRules<SourceNode> {

    public SourceConverter(Class<DslNode> in, Class<SourceNode> out, String description) {
      super(in, out, description);
    }

    @Override
    public SourceNode convertNode(
        DslNode node,
        TraverserContext traverserContext,
        ConverterContext converterContext) {
      try {
        if (node.getObjectClass().isInstance(BaseNode.class)) {
          String vertexId = node.getVertexDef().getId();
          if (traverserContext.getTopologyDef().getSources().containsKey(vertexId)) {
            return SourceNode.copy((BaseNode) node, SourceNode.class);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      throw new UnsupportedOperationException("conversion can only applied for BaseNode!");
    }
  }

  public static class SinkConverter extends ExampleDslConversionRules<SinkNode> {

    public SinkConverter(Class<DslNode> in, Class<SinkNode> out, String description) {
      super(in, out, description);
    }

    @Override
    public SinkNode convertNode(
        DslNode node,
        TraverserContext traverserContext,
        ConverterContext converterContext) {
      try {
        if (node.getObjectClass().isInstance(BaseNode.class)) {
          String vertexId = node.getVertexDef().getId();
          if (traverserContext.getTopologyDef().getSinks().containsKey(vertexId)) {
            return SinkNode.copy((BaseNode) node, SinkNode.class);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      throw new UnsupportedOperationException("conversion can only applied for BaseNode!");
    }
  }

  public static class OperatorConverter extends ExampleDslConversionRules<OperatorNode> {

    public OperatorConverter(Class<DslNode> in, Class<OperatorNode> out, String description) {
      super(in, out, description);
    }

    @Override
    public OperatorNode convertNode(
        DslNode node,
        TraverserContext traverserContext,
        ConverterContext converterContext) {
      try {
        if (node.getObjectClass().isInstance(BaseNode.class)) {
          String vertexId = node.getVertexDef().getId();
          if (traverserContext.getTopologyDef().getOperators().containsKey(vertexId)) {
            return OperatorNode.copy((BaseNode) node, OperatorNode.class);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      throw new UnsupportedOperationException("conversion can only applied for BaseNode!");
    }
  }
}
