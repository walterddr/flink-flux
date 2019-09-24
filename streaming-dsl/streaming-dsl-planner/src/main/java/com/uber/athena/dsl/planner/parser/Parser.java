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

package com.uber.athena.dsl.planner.parser;

import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.utils.ParsingException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parse into a {@link Topology} from its stringify, user-friendly format.
 */
public interface Parser {

  // TODO @walterddr: make the parser interface more unified. instead of supporting
  // different types of input, support only java.io.Reader and let it figure out
  // how to deal with different types of Reader impl.

  Topology parseFile(
      String inputFile,
      boolean dumpYaml,
      String propertiesFile,
      boolean envSub) throws IOException, ParsingException;

  Topology parseResource(
      String resource,
      boolean dumpYaml,
      String propertiesFile,
      boolean envSub) throws IOException, ParsingException;

  Topology parseInputStream(
      InputStream inputStream,
      boolean dumpYaml,
      String propertiesFile,
      boolean envSub) throws ParsingException;
}
