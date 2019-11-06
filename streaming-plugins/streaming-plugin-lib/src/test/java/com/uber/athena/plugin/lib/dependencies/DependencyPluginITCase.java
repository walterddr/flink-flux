/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.uber.athena.plugin.lib.dependencies;

import com.uber.athena.plugin.api.Executor;
import com.uber.athena.plugin.api.PluginResult;
import com.uber.athena.plugin.executor.direct.DirectInvokeExecutor;
import com.uber.athena.plugin.executor.process.ProcessExecutor;
import com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginPayload;
import com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginResult;
import com.uber.athena.plugin.payload.ExecutorPayloadImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Integration test - dependency plugin executions to resolving dependencies.
 */
public class DependencyPluginITCase {

  @Test
  public void testDirectExecution() throws Exception {
    Executor executor = new DirectInvokeExecutor();
    DependencyPluginPayload payload = new DependencyPluginPayload.Builder()
        .setArtifactList("commons-io:commons-io:2.6")
        .build();
    PluginResult res = executor.run(
        new ExecutorPayloadImpl(DependencyPlugin.class.getName(), payload));
    Assert.assertTrue(res instanceof DependencyPluginResult);
  }

  @Test
  public void testProcessExecutor() throws Exception {
    Executor executor = new ProcessExecutor();
    DependencyPluginPayload payload = new DependencyPluginPayload.Builder()
        .setArtifactList("commons-io:commons-io:2.6")
        .build();
    PluginResult res = executor.run(
        new ExecutorPayloadImpl(DependencyPlugin.class.getName(), payload));
    Assert.assertTrue(res instanceof DependencyPluginResult);
  }

}
