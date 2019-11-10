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
import com.uber.athena.plugin.lib.dsl.payload.FlinkPluginDependencyResult;
import com.uber.athena.plugin.lib.dsl.runtime.FlinkExecuteJobGraphPlugin;
import com.uber.athena.plugin.lib.dsl.runtime.FlinkExecutePluginPayload;
import com.uber.athena.plugin.lib.dsl.runtime.FlinkExecutePluginResult;
import com.uber.athena.plugin.payload.ExecutorPayloadImpl;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Integration test for DSL plugin executions.
 */
public class FlinkDslPluginExecutorITCase extends DslPluginTestBase {

  public FlinkDslPluginExecutorITCase(String name, File file) {
    super(name, file);
  }

  @Test
  public void testDslPluginConstructorDirectInvoke() throws Exception {
    DirectInvokeExecutor executor = new DirectInvokeExecutor();
    PluginResult buildResult = executor.run(new ExecutorPayloadImpl(
        FlinkDslConstructPlugin.class.getName(),
        generatePayload()));
    JobGraph jobGraph = ((FlinkPluginConstructionResult) buildResult).getJobGraph();
    PluginResult runResult = executor.run(new ExecutorPayloadImpl(
        FlinkExecuteJobGraphPlugin.class.getName(),
        new FlinkExecutePluginPayload(jobGraph)));
    Assert.assertTrue(runResult instanceof FlinkExecutePluginResult);
  }

  @Test
  public void testDslPluginConstructorProcessExecute() throws Exception {
    ProcessExecutor executor = new ProcessExecutor();
    PluginResult buildResult = executor.run(new ExecutorPayloadImpl(
        FlinkDslConstructPlugin.class.getName(),
        generatePayload()));
    JobGraph jobGraph = ((FlinkPluginConstructionResult) buildResult).getJobGraph();

    // run the job graph.
    ProcessExecutor runExecutor = new ProcessExecutor();
    PluginResult runResult = runExecutor.run(new ExecutorPayloadImpl(
        FlinkExecuteJobGraphPlugin.class.getName(),
        new FlinkExecutePluginPayload(jobGraph)));
    Assert.assertTrue(runResult instanceof FlinkExecutePluginResult);
  }

  @Test
  public void testDslPluginDownloadDependencyThenExecute() throws Exception {
    ProcessExecutor depExecutor = new ProcessExecutor();

    // download dependencies.
    PluginResult depResult = depExecutor.run(new ExecutorPayloadImpl(
        FlinkDslDependencyPlugin.class.getName(),
        generatePayload()));
    Assert.assertTrue(depResult instanceof FlinkPluginDependencyResult);

    // build job graph.
    ProcessExecutor buildExecutor = new ProcessExecutor(
        "java.home",
        "java.class.path",
        ((FlinkPluginDependencyResult) depResult).getClassPath(),
        ProcessExecutor.ConnectedStreamType.SOCKET,
        ProcessExecutor.ConnectedStreamType.SOCKET
    );

    PluginResult buildResult = buildExecutor.run(new ExecutorPayloadImpl(
        FlinkDslConstructPlugin.class.getName(),
        generatePayload()));
    Assert.assertTrue(buildResult instanceof FlinkPluginConstructionResult);
    JobGraph jobGraph = ((FlinkPluginConstructionResult) buildResult).getJobGraph();

    // run the job graph.
    ProcessExecutor runExecutor = new ProcessExecutor(
        "java.home",
        "java.class.path",
        ((FlinkPluginDependencyResult) depResult).getClassPath(),
        ProcessExecutor.ConnectedStreamType.SOCKET,
        ProcessExecutor.ConnectedStreamType.SOCKET
    );

    PluginResult runResult = runExecutor.run(new ExecutorPayloadImpl(
        FlinkExecuteJobGraphPlugin.class.getName(),
        new FlinkExecutePluginPayload(jobGraph)));
    Assert.assertTrue(runResult instanceof FlinkExecutePluginResult);
  }
}
