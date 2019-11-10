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
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Integration test - dependency plugin executions to resolving dependencies.
 */
public class DependencyPluginITCase {
  private static final String GROUP_ID = "commons-io";
  private static final String ARTIFACT_ID = "commons-io";
  private static final String VERSION = "2.6";
  private static Path testDirForLocalDirOption;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    testDirForLocalDirOption = Files.createTempDirectory("dependency-resolver-test-dir");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    FileUtils.deleteQuietly(testDirForLocalDirOption.toFile());
  }

  @Test
  public void testUsingDirectExecutor() throws Exception {
    Executor executor = new DirectInvokeExecutor();
    DependencyPluginPayload payload = new DependencyPluginPayload.Builder()
        .setArtifactList(String.join(":", GROUP_ID, ARTIFACT_ID, VERSION))
        .setMavenLocalDir(testDirForLocalDirOption.toString())
        .build();
    PluginResult res = executor.run(
        new ExecutorPayloadImpl(DependencyPlugin.class.getName(), payload));
    assertResults(res);
  }

  @Test
  public void testUsingProcessExecutor() throws Exception {
    Executor executor = new ProcessExecutor();
    DependencyPluginPayload payload = new DependencyPluginPayload.Builder()
        .setArtifactList(String.join(":", GROUP_ID, ARTIFACT_ID, VERSION))
        .setMavenLocalDir(testDirForLocalDirOption.toString())
        .build();
    PluginResult res = executor.run(
        new ExecutorPayloadImpl(DependencyPlugin.class.getName(), payload));
    assertResults(res);
  }

  private static void assertResults(PluginResult<?> res) throws AssertionError {
    Assert.assertTrue(res instanceof DependencyPluginResult);
    DependencyPluginResult result = (DependencyPluginResult) res;
    Assert.assertEquals(1, result.getArtifactToPathMapping().size());
    List<Map.Entry<String, String>> correctArtifacts = result.getArtifactToPathMapping()
        .entrySet()
        .stream()
        .filter(e -> e.getKey().startsWith(String.join(":", GROUP_ID, ARTIFACT_ID)))
        .collect(Collectors.toList());
    Assert.assertEquals(1, correctArtifacts.size());
    Assert.assertTrue(correctArtifacts.get(0)
        .getValue()
        .startsWith(testDirForLocalDirOption.toString()));
  }
}
