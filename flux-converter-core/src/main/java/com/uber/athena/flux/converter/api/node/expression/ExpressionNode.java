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

package com.uber.athena.flux.converter.api.node.expression;

import com.uber.athena.flux.converter.api.node.Node;

/**
 * Node that represents the constructed DAG transformation towards this vertex.
 *
 * <p>{@code ExpressionNode} serves as a planner result constructed from
 * sources of this DAG until this specific vertex. result is platform-specific
 * E.g. they should:
 *
 * <p><ul>
 * <li>Abstract: the planner result is represented in one-single expression.
 * <li>Consolidated: downstream expression only depends on upstream expression.
 * </ul></p>
 */
public interface ExpressionNode extends Node {

  Object getExpression();
}
