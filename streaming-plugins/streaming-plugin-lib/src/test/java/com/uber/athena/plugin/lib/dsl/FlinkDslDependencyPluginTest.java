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

package com.uber.athena.plugin.lib.dsl;

import com.uber.athena.plugin.api.PluginResult;
import com.uber.athena.plugin.lib.dsl.payload.FlinkPluginDependencyResult;
import com.uber.athena.plugin.lib.dsl.payload.FlinkPluginPayload;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Test DSL plugins without invoking executor.
 */
public class FlinkDslDependencyPluginTest extends DslPluginTestBase {

  public FlinkDslDependencyPluginTest(String name, File file) {
    super(name, file);
  }

  @Test
  public void testDslPlugin() throws Exception {
    FlinkDslDependencyPlugin plugin = new FlinkDslDependencyPlugin();
    FlinkPluginPayload payload = generatePayload();
    plugin.instantiate(payload);
    PluginResult res = plugin.run();
    assertResults(res);
  }

  private static void assertResults(PluginResult<?> res) throws AssertionError {
    Assert.assertTrue(res instanceof FlinkPluginDependencyResult);
    FlinkPluginDependencyResult result = (FlinkPluginDependencyResult) res;
    Assert.assertNotNull(result.getClassPath());
    Assert.assertTrue(result.getArtifacts().size() > 0);
  }
}
