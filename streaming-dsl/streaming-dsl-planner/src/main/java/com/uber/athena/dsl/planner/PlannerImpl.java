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

import com.uber.athena.dsl.planner.element.ElementBuilder;
import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.parser.Parser;
import com.uber.athena.dsl.planner.relation.RelationBuilder;
import com.uber.athena.dsl.planner.relation.RelationNode;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.utils.ConstructionException;
import com.uber.athena.dsl.planner.utils.ParsingException;
import com.uber.athena.dsl.planner.utils.ValidationException;
import com.uber.athena.dsl.planner.validation.Validator;

import java.io.InputStream;
import java.util.Map;

/**
 * Standard {@link Planner} implementation.
 */
public class PlannerImpl implements Planner {

  private Parser parser;
  private Validator validator;
  private ElementBuilder elementBuilder;
  private RelationBuilder relationBuilder;

  // TODO @walterddr create wrapper for planner construction instead of directly
  // creating each individual modules.

  public PlannerImpl(
      Parser parser,
      Validator validator,
      ElementBuilder elementBuilder,
      RelationBuilder relationBuilder
  ) {
    this.parser = parser;
    this.validator = validator;
    this.elementBuilder = elementBuilder;
    this.relationBuilder = relationBuilder;
  }

  @Override
  public Topology parse(InputStream stream) throws ParsingException {
    // fix config utilization.
    return parser.parseInputStream(stream, false, null, false);
  }

  @Override
  public Topology validate(Topology topology) throws ValidationException {
    validator.validate(topology);
    return topology;
  }

  @Override
  public Map<String, ? extends ElementNode> constructElement(
      Topology topology) throws ConstructionException {
    return elementBuilder.construct(topology);
  }

  @Override
  public Map<String, ? extends RelationNode> constructRelation(
      Topology topology,
      Map<String, ElementNode> elementMapping) throws ConstructionException {
    return relationBuilder.construct(topology, elementMapping);
  }
}
