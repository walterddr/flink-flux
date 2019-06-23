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

package com.uber.athena.flux.converter.api.traverser;

/**
 * Traverser defines the way to organize the conversion of all nodes in the DAG.
 *
 * <p>{@code Traverser} is used to perform one or several steps to convert nodes,
 * the type of conversion methods depends on the input and desired output node
 * types, as well as the dynamic rules applied on the program.
 *
 * <p>The program determines the order of conversion applied to the nodes in
 * a DAG. Once the order of conversion is determined, program should invoke
 * {@code Converter} to perform the conversion.
 */
public interface Traverser {

  /**
   * Run the program to perform its designated actions.
   */
  void run();

  /**
   * Validate that the traverse program is ready to run.
   */
  void validate();
}
