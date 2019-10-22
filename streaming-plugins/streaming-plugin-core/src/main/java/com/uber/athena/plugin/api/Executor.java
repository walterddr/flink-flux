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

package com.uber.athena.plugin.api;

import java.io.IOException;

/**
 * An executor executes a {@link Plugin} and returns its desired output.
 *
 * <p>Plugin should be instantiable by the executor with the
 * {@link PluginPayload}.
 */
public interface Executor {

  /**
   * Executes this executor and generates some sort of executor results.
   *
   * @param payload the payload used to instantiate the executor.
   * @return the {@link PluginResult} this executor generates.
   * @throws IOException when executor failure occurs.
   */
  PluginResult run(ExecutorPayload payload) throws IOException;
}
