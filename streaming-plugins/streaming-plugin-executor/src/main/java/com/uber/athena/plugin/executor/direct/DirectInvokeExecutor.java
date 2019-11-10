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

package com.uber.athena.plugin.executor.direct;

import com.uber.athena.plugin.api.Executor;
import com.uber.athena.plugin.api.ExecutorPayload;
import com.uber.athena.plugin.api.Plugin;
import com.uber.athena.plugin.api.PluginResult;
import com.uber.athena.plugin.base.ExceptionPluginResult;

import java.io.IOException;

/**
 * An impl of the {@link Executor} that uses a separated system process.
 */
public class DirectInvokeExecutor implements Executor {

  /**
   * Directly invoke a {@link Plugin} for processing.
   *
   * <p>The plugin should be properly instantiated via by its desired
   * {@link com.uber.athena.plugin.api.PluginPayload}. Executor does not
   * check whether the plugin is executable.
   *
   * @return the plugin result after execution.
   * @throws IOException when direct execution fails.
   */
  @Override
  @SuppressWarnings("unchecked")
  public PluginResult run(ExecutorPayload payload) throws IOException {
    try {
      Plugin plugin = payload.getPluginClazz().getDeclaredConstructor().newInstance();
      plugin.instantiate(payload.getPluginPayload());
      return plugin.run();
    } catch (Exception e) {
      return new ExceptionPluginResult(
          new IOException("Unable to execute plugin directly!", e));
    }
  }
}
