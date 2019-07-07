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

package com.uber.athena.flux.converter.impl.testutils.expression;

import com.uber.athena.flux.converter.impl.testutils.operator.Operator;
import com.uber.athena.flux.model.VertexDef;

import java.util.Map;

/**
 * Traversing expression that backtracks from current node to source.
 *
 * <p>This expression does not actually perform any processing, but rather
 * represents a "description" of the traverse tree. It provides an "explanation"
 * of the topology generated by the DSL.
 */
public class TraverseTreeExpression extends Expression {

  private static final int INDENT_SIZE = 4;
  private final String vertexId;
  private final VertexDef vertexDef;
  private final Operator operator;
  private final Map<String, Expression> upstreams;

  public TraverseTreeExpression(
      String vertexId,
      VertexDef vertexDef,
      Operator operator,
      Map<String, Expression> upstreams
  ) {
    this.vertexId = vertexId;
    this.vertexDef = vertexDef;
    this.operator = operator;
    this.upstreams = upstreams;
    this.description = computeDescription();
  }

  public Operator getOperator() {
    return operator;
  }

  public Map<String, Expression> getUpstreams() {
    return upstreams;
  }

  public String getDescription() {
    return description;
  }

  public VertexDef getVertexDef() {
    return vertexDef;
  }

  public String computeDescription() {
    final StringBuilder sb = new StringBuilder();
    explainExpression(sb, this, 0);
    return sb.toString();
  }

  private static void explainExpression(
      StringBuilder sb,
      Expression expression,
      int currentIndent) {
    // Explain current operator
    expression.getOperator().computeDescription();
    String description = expression.getOperator().getDescription();
    sb.append(makeIndent(currentIndent));
    sb.append('{');
    sb.append(description);
    sb.append("\n");
    for (Expression exp: expression.getUpstreams().values()) {
      explainExpression(sb, exp, currentIndent + INDENT_SIZE);
    }
  }

  public static String makeIndent(int indent) {
    String spacing = new String(new char[indent]).replace('\0', ' ');
    return spacing + "|-->";
  }
}