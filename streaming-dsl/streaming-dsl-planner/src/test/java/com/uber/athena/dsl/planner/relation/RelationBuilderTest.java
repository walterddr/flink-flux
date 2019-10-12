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

package com.uber.athena.dsl.planner.relation;

import com.uber.athena.dsl.planner.PlannerTestBase;
import com.uber.athena.dsl.planner.element.ElementBuilder;
import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.element.constructor.ConstructorImpl;
import com.uber.athena.dsl.planner.parser.DslParser;
import com.uber.athena.dsl.planner.parser.Parser;
import com.uber.athena.dsl.planner.relation.rule.RuleExecutorImpl;
import com.uber.athena.dsl.planner.relation.rule.ruleset.StandardRuleSet;
import com.uber.athena.dsl.planner.topology.DslTopologyBuilder;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.type.TypeFactoryImpl;
import com.uber.athena.dsl.planner.validation.DslValidator;
import com.uber.athena.dsl.planner.validation.Validator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Map;

/**
 * Test for {@link ElementBuilder}.
 */
@SuppressWarnings("unchecked")
public class RelationBuilderTest extends PlannerTestBase {

  private static Parser parser;
  private static Validator validator;
  private static ElementBuilder elementBuilder;
  private static RelationBuilder relationBuilder;

  public RelationBuilderTest(String name, File file) {
    super(name, file);
  }

  @BeforeClass
  public static void setUp() throws Exception {
    validator = new DslValidator();
    parser = new DslParser(null, new DslTopologyBuilder());
    elementBuilder = new ElementBuilder(
        null,
        new ConstructorImpl(),
        new TypeFactoryImpl());
    relationBuilder = new RelationBuilder(
        null,
        new RuleExecutorImpl(StandardRuleSet.getInstance(), null));
  }

  @Test
  public void testRelationBuilder() throws Exception {
    Topology topology = parser.parseFile(file.getCanonicalPath(), false, null, false);
    Topology validatedTopology = validator.validate(topology);
    Map<String, ElementNode> elementMapping =
        (Map<String, ElementNode>) elementBuilder.construct(validatedTopology);
    relationBuilder.construct(validatedTopology, elementMapping);
  }
}
