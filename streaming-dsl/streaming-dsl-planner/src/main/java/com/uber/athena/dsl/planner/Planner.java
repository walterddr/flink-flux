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

package com.uber.athena.dsl.planner;

import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.relation.RelationNode;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.utils.ConstructionException;
import com.uber.athena.dsl.planner.utils.ParsingException;
import com.uber.athena.dsl.planner.utils.ValidationException;

import java.io.InputStream;
import java.util.Map;

/**
 * Main interface of the planner framework.
 *
 * <p>The planner framework is the main entrance to process streaming DSL.
 * It is designed as a context holder for all configurations/plugins related
 * to composing streaming application. Including:
 *
 * <p>It is essentially a Facade design pattern to hide all complex modules
 * within the DSL planner framework:
 *
 * <p><ul>
 * <li>{@link com.uber.athena.dsl.planner.parser.Parser}
 * <li>{@link com.uber.athena.dsl.planner.validation.Validator}
 * <li>{@link com.uber.athena.dsl.planner.element.ElementBuilder}
 * <li>{@link com.uber.athena.dsl.planner.relation.RelationBuilder}
 * <li>...
 * </ul></p>
 *
 */
public interface Planner {

  /**
   * Parser an input stream and constructs a {@link Topology}.
   *
   * @param stream input stream
   * @return topology constructed.
   * @throws ParsingException when parsing of the input stream fails.
   */
  Topology parse(InputStream stream) throws ParsingException;

  /**
   * Validate that the topology is correct.
   *
   * <p>Validation does not try to resolve any relations or any elements
   * that requires construction, it only validates the semantic.
   *
   * @param topology topology representing the DSL model.
   * @return topology after validation process completes.
   * @throws ValidationException validation fails.
   */
  Topology validate(Topology topology) throws ValidationException;

  /**
   * Construct the {@link ElementNode}s for all vertices.
   *
   * @param topology topology definition of the DSL model.
   * @return the constructed elementNode for each vertex.
   * @throws ConstructionException when construction fails.
   */
  Map<String, ? extends ElementNode> constructElement(
      Topology topology) throws ConstructionException;

  /**
   * Construct the {@link RelationNode} against a particular runtime environment.
   *
   * @param topology topology definition of the DSL model.
   * @param elementMapping the constructed element nodes.
   * @return the constructed relation node for each vertex.
   * @throws ConstructionException when construction fails.
   */
  Map<String, ? extends RelationNode> constructRelation(
      Topology topology,
      Map<String, ElementNode> elementMapping) throws ConstructionException;
}
