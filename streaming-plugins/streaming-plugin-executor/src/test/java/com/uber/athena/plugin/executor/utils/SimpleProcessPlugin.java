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

package com.uber.athena.plugin.executor.utils;

import com.uber.athena.plugin.api.Plugin;
import com.uber.athena.plugin.api.PluginPayload;
import com.uber.athena.plugin.api.PluginResult;

import java.io.IOException;

/**
 * Test {@link Plugin}.
 */
public class SimpleProcessPlugin implements Plugin {
  private PluginPayload pluginPayload;

  @Override
  public void instantiate(PluginPayload payload) {
    this.pluginPayload = payload;
  }

  @Override
  public PluginResult run() throws Exception {
    SimpleProcessPluginPayload payload = (SimpleProcessPluginPayload) pluginPayload;
    SimpleProcessPluginResult pluginResult = new SimpleProcessPluginResult();
    try {
      int processResult = examplePluginProcessFunction(payload);
      pluginResult.setResult(processResult);
    } catch (Exception e) {
      pluginResult.setException(e);
    }
    return pluginResult;
  }

  private static int examplePluginProcessFunction(
      SimpleProcessPluginPayload payload) throws Exception {
    String str = payload.getMyStrField();
    try {
      int val = Integer.parseInt(str);
      return val + payload.getMyIntField();
    } catch (NumberFormatException ne) {
      throw new IOException("not a number", ne);
    }
  }
}
