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

import com.uber.athena.plugin.api.PluginResult;
import com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginPayload;
import com.uber.athena.plugin.lib.dependencies.payload.DependencyPluginResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static com.uber.athena.plugin.lib.dependencies.ResolverResultValidateUtils.assertArtifactPathMapContains;
import static org.junit.Assert.fail;

/**
 * Test for dependency plugin executions.
 */
public class DependencyPluginTest {

  @Test
  public void testResolvingDependencies() throws Exception {
    DependencyPluginPayload payload = new DependencyPluginPayload.Builder()
        .setArtifactList("commons-io:commons-io:2.6")
        .build();
    DependencyPlugin plugin = new DependencyPlugin();
    plugin.instantiate(payload);
    PluginResult res = plugin.run();
    Assert.assertTrue(res instanceof DependencyPluginResult);
    assertArtifactPathMapContains(
        ((DependencyPluginResult) res).getArtifactToPathMapping(),
        "commons-io",
        "commons-io"
    );
  }

  @Test
  public void testResolvingDependencyWithTransDependencies() throws Exception {
    DependencyPluginPayload payload = new DependencyPluginPayload.Builder()
        .setArtifactList("com.fasterxml.jackson.core:jackson-databind:2.9.10")
        .build();
    DependencyPlugin plugin = new DependencyPlugin();
    plugin.instantiate(payload);
    PluginResult res = plugin.run();
    Assert.assertTrue(res instanceof DependencyPluginResult);
    Map<String, String> pathMapping = ((DependencyPluginResult) res).getArtifactToPathMapping();
    assertArtifactPathMapContains(pathMapping, "com.fasterxml.jackson.core", "jackson-databind");
    assertArtifactPathMapContains(pathMapping, "com.fasterxml.jackson.core", "jackson-core");
  }

  @Test
  public void testResolvingDependencyWithExclusion() throws Exception {
    DependencyPluginPayload payload = new DependencyPluginPayload.Builder()
        .setArtifactList("com.fasterxml.jackson.core:jackson-databind:2.9.10"
            + "^com.fasterxml.jackson.core:jackson-core")
        .build();
    DependencyPlugin plugin = new DependencyPlugin();
    plugin.instantiate(payload);
    PluginResult res = plugin.run();
    Assert.assertTrue(res instanceof DependencyPluginResult);
    Map<String, String> pathMapping = ((DependencyPluginResult) res).getArtifactToPathMapping();
    assertArtifactPathMapContains(pathMapping, "com.fasterxml.jackson.core", "jackson-databind");
    try {
      assertArtifactPathMapContains(pathMapping, "com.fasterxml.jackson.core", "jackson-core");
      // should not resolve core because of exclusion.
      fail();
    } catch (AssertionError ae) {
      // expected.
    }
  }
}
