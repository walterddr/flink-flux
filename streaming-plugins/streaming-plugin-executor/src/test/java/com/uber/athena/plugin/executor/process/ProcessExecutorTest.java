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

package com.uber.athena.plugin.executor.process;

import com.uber.athena.plugin.api.Executor;
import com.uber.athena.plugin.api.PluginResult;
import com.uber.athena.plugin.executor.utils.SimpleProcessPlugin;
import com.uber.athena.plugin.executor.utils.SimpleProcessPluginPayload;
import com.uber.athena.plugin.executor.utils.SimpleProcessPluginResult;
import com.uber.athena.plugin.payload.ExecutorPayloadImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ProcessExecutorTest {

  @Test
  public void testDefault() throws Exception {
    SimpleProcessPluginPayload payload = new SimpleProcessPluginPayload("23", 4);
    Executor executor = new ProcessExecutor();
    PluginResult result = executor.run(new ExecutorPayloadImpl(
        SimpleProcessPlugin.class.getName(),
        payload));
    Assert.assertTrue(result instanceof SimpleProcessPluginResult);
    int actual = (Integer) ((SimpleProcessPluginResult) result).getResult();
    Assert.assertEquals(27, actual);
  }

  @Test
  public void testProcessWithAllSystemStreams() throws Exception {
    SimpleProcessPluginPayload payload = new SimpleProcessPluginPayload("23", 4);
    Executor executor = new ProcessExecutor(
        ProcessExecutor.ConnectedStreamType.SYSTEM,
        ProcessExecutor.ConnectedStreamType.SYSTEM);
    PluginResult result = executor.run(new ExecutorPayloadImpl(
        SimpleProcessPlugin.class.getName(),
        payload));
    Assert.assertTrue(result instanceof SimpleProcessPluginResult);
    int actual = (Integer) ((SimpleProcessPluginResult) result).getResult();
    Assert.assertEquals(27, actual);
  }

  @Test
  public void testInvalidPayload() throws Exception {
    SimpleProcessPluginPayload payload = new SimpleProcessPluginPayload("asdf", 4);
    Executor executor = new ProcessExecutor();
    PluginResult result = executor.run(new ExecutorPayloadImpl(
        SimpleProcessPlugin.class.getName(),
        payload));
    Assert.assertTrue(result instanceof SimpleProcessPluginResult);
    Assert.assertTrue(((SimpleProcessPluginResult) result).getException() instanceof IOException);
  }
}
