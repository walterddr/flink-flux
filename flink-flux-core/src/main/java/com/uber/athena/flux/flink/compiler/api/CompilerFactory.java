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

package com.uber.athena.flux.flink.compiler.api;

import java.util.Map;

/**
 * The compiler factory that finds and creates appropriate compiler.
 *
 * <p>Usually they are associated with a specific {@code CompilerVertex}.
 */
public interface CompilerFactory {

  /**
   * create a compiler based on provided object classes defined in Flux.
   *
   * <p>Additional properties can be provided to identify the proper
   * compiler associated with the class.
   *
   * @param objectClass the target object class defined in the Flux topology
   * @param properties additional properties.
   * @return the compiler for the object.
   */
  Compiler<?> getCompiler(
      Class<?> objectClass,
      Map<String, String> properties);

  Compiler<?> getCompiler(
      String className,
      Map<String, String> properties);
}
