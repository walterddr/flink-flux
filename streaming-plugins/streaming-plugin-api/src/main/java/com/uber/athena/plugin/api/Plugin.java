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

/**
 * A plugin is an executable piece of encapsulation used by {@link Executor}.
 *
 * <p>A plugin should be self-contained, thus it should have all the necessary
 * properties, objects and arguments set during construction.
 */
public interface Plugin {

  /**
   * Instantiate the plugin with a specific payload.
   *
   * <p>The payload is used to describe the execution input.
   * It should also contain any configurations needed by the the plugin for
   * properly configure its internal states.
   *
   * @param payload the plugin payload used to instantiate this plugin.
   */
  void instantiate(PluginPayload payload);

  /**
   * Runs the plugin and produces the plugin result.
   *
   * <p>plugin run result must extends {@link java.io.Serializable}.
   *
   * @return the plugin result.
   * @throws Exception when executor exception occurs.
   */
  PluginResult run() throws Exception;
}
