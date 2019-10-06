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
 *
 */

package com.uber.athena.dsl.planner.relation.rule;

import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.model.StreamDef;
import com.uber.athena.dsl.planner.model.VertexNode;
import com.uber.athena.dsl.planner.relation.RelationNode;
import com.uber.athena.dsl.planner.utils.ConstructionException;

import java.util.Map;

/**
 * Base implementation of a RuleExecutor.
 *
 * <p>Concrete implementation of an executor should depend on a concrete
 * implementation of {@link RuleCall}.
 */
public interface RuleExecutor {

  /**
   * Executes a {@link RuleSet} on a specific {@link VertexNode}.
   *
   * @param vertex the vertex node in the DSL model.
   * @param elementNode the element node constructed as an object.
   * @param streamDefs the upstream stream definitions
   * @param relations the upstream relation nodes.
   * @return the relation node representing the current vertex.
   * @throws ConstructionException when relation cannot be constructed.
   */
  RelationNode convert(
      VertexNode vertex,
      ElementNode elementNode,
      Map<String, StreamDef> streamDefs,
      Map<String, RelationNode> relations) throws ConstructionException;
}
