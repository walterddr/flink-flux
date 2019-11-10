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

package com.uber.athena.plugin.lib.dsl;

import com.uber.athena.plugin.api.PluginResult;
import com.uber.athena.plugin.executor.direct.DirectInvokeExecutor;
import com.uber.athena.plugin.executor.process.ProcessExecutor;
import com.uber.athena.plugin.lib.dsl.payload.FlinkPluginConstructionResult;
import com.uber.athena.plugin.payload.ExecutorPayloadImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Test DSL plugins and validate the results.
 */
public class FlinkDslConstructPluginTest extends DslPluginTestBase {

  public FlinkDslConstructPluginTest(String name, File file) {
    super(name, file);
  }

  @Test
  public void testDslPluginConstructor() throws Exception {
    DirectInvokeExecutor executor = new DirectInvokeExecutor();
    PluginResult res = executor.run(new ExecutorPayloadImpl(
        FlinkDslConstructPlugin.class.getName(),
        generatePayload()));
    Assert.assertTrue(res instanceof FlinkPluginConstructionResult);
    Assert.assertNotNull(((FlinkPluginConstructionResult) res).getJobGraph());
  }

  @Test
  public void testDslPluginConstructorProcessExecute() throws Exception {
    ProcessExecutor executor = new ProcessExecutor();
    PluginResult res = executor.run(new ExecutorPayloadImpl(
        FlinkDslConstructPlugin.class.getName(),
        generatePayload()));
    Assert.assertTrue(res instanceof FlinkPluginConstructionResult);
    Assert.assertNotNull(((FlinkPluginConstructionResult) res).getJobGraph());
  }
}
