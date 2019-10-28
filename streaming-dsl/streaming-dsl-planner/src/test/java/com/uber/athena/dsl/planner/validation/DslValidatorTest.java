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

package com.uber.athena.dsl.planner.validation;

import com.uber.athena.dsl.planner.PlannerComponentTestBase;
import com.uber.athena.dsl.planner.parser.DslParser;
import com.uber.athena.dsl.planner.parser.Parser;
import com.uber.athena.dsl.planner.topology.DslTopologyBuilder;
import com.uber.athena.dsl.planner.topology.Topology;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

/**
 * Test for {@link Validator}.
 */
public class DslValidatorTest extends PlannerComponentTestBase {

  private static Parser parser;
  private static Validator validator;

  public DslValidatorTest(String name, File file) {
    super(name, file);
  }

  @BeforeClass
  public static void setUp() throws Exception {
    validator = new DslValidator();
    parser = new DslParser(null, new DslTopologyBuilder());
  }

  @Test
  public void testValidator() throws Exception {
    Topology topology = parser.parseFile(file.getCanonicalPath(), false, null, false);
    validator.validate(topology);
  }
}
