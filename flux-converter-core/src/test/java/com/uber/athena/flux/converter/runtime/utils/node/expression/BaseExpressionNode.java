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

package com.uber.athena.flux.converter.runtime.utils.node.expression;

import com.uber.athena.flux.converter.api.node.expression.ExpressionNode;
import com.uber.athena.flux.model.VertexDef;

public abstract class BaseExpressionNode implements ExpressionNode {

  protected Object constructedExpression;

  protected String vertexId;
  protected VertexDef vertexDef;

  public BaseExpressionNode(String vertexId, VertexDef vertexDef) {
    this.vertexId = vertexId;
    this.vertexDef = vertexDef;
  }

  @Override
  public String getVertexId() {
    return vertexId;
  }

  @Override
  public VertexDef getVertexDef() {
    return vertexDef;
  }

  @Override
  public Object getExpression() {
    return constructedExpression;
  }

  public void setExpression(Object expression) {
    this.constructedExpression = expression;
  }
}
