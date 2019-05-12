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

import com.uber.athena.flux.flink.compiler.impl.datastream.FluxContext;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Compile a specific component into executable DataStream elements.
 *
 * <p>This compiler main interface does not provide any concrete compilation interface
 * as the actual compilation result varies depends on the API level selected.
 *
 * <p>This interface is only used as the based component of all compilation extensions.
 */
public interface Compiler {

  /**
   * Compile the thing.
   *
   * @param senv        stream execution environment
   * @param fluxContext flux context
   * @param vertex      compilation vertex.
   */
  void compile(StreamExecutionEnvironment senv, FluxContext fluxContext, CompilerVertex vertex);
}
