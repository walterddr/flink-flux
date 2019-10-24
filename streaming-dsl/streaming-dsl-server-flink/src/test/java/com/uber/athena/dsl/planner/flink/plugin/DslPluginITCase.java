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

package com.uber.athena.dsl.planner.flink.plugin;

import com.uber.athena.dsl.planner.flink.PlannerITCaseBase;
import com.uber.athena.plugin.api.PluginResult;
import com.uber.athena.plugin.executor.direct.DirectInvokeExecutor;
import com.uber.athena.plugin.executor.process.ProcessExecutor;
import com.uber.athena.plugin.payload.ExecutorPayloadImpl;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.minicluster.MiniCluster;
import org.apache.flink.runtime.minicluster.MiniClusterConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Integration test for DSL plugin executions.
 */
public class DslPluginITCase extends PlannerITCaseBase {

  public DslPluginITCase(String name, File file) {
    super(name, file);
  }

  @Test
  public void testDslPluginConstructorDirectInvoke() throws Exception {
    DirectInvokeExecutor executor = new DirectInvokeExecutor();
    PluginResult res = executor.run(new ExecutorPayloadImpl(
        FlinkDslConstructPlugin.class.getName(),
        generatePayload()));
    JobGraph jobGraph = ((FlinkPluginResult) res).getJobGraph();
    executeJobGraph(jobGraph);
  }

  @Test
  public void testDslPluginConstructorProcessExecute() throws Exception {
    ProcessExecutor executor = new ProcessExecutor();
    PluginResult res = executor.run(new ExecutorPayloadImpl(
        FlinkDslConstructPlugin.class.getName(),
        generatePayload()));
    JobGraph jobGraph = ((FlinkPluginResult) res).getJobGraph();
    executeJobGraph(jobGraph);
  }

  private FlinkPluginPayload generatePayload() {
    Configuration flinkConf = new Configuration();
    Properties properties = new Properties();
    Map<String, Object> config = Collections.singletonMap("_JOB_PARALLELISM", "1");
    return new FlinkPluginPayload(
        file.getAbsolutePath(),
        FlinkPluginPayload.PluginRuleSetType.DATASTREAM,
        flinkConf,
        config,
        properties
    );
  }

  private void executeJobGraph(JobGraph jobGraph) throws Exception {
    TestLocalStreamEnvironment sEnv = new TestLocalStreamEnvironment();
    sEnv.execute(jobGraph);
  }

  private class TestLocalStreamEnvironment extends StreamExecutionEnvironment {
    private final Configuration configuration;

    TestLocalStreamEnvironment() {
      this(new Configuration());
    }

    TestLocalStreamEnvironment(Configuration configuration) {
      this.configuration = configuration;
    }

    @Override
    public JobExecutionResult execute(StreamGraph streamGraph) throws Exception {
      return execute(streamGraph.getJobGraph());
    }

    public JobExecutionResult execute(JobGraph jobGraph) throws Exception {
      jobGraph.setAllowQueuedScheduling(true);
      Configuration configuration = new Configuration();
      configuration.addAll(jobGraph.getJobConfiguration());
      configuration.setString(TaskManagerOptions.MANAGED_MEMORY_SIZE, "0");
      configuration.addAll(this.configuration);
      if (!configuration.contains(RestOptions.BIND_PORT)) {
        configuration.setString(RestOptions.BIND_PORT, "0");
      }

      int numSlotsPerTaskManager = configuration.getInteger(
          TaskManagerOptions.NUM_TASK_SLOTS, jobGraph.getMaximumParallelism());
      MiniClusterConfiguration cfg = (new MiniClusterConfiguration.Builder())
          .setConfiguration(configuration)
          .setNumSlotsPerTaskManager(numSlotsPerTaskManager)
          .build();
      MiniCluster miniCluster = new MiniCluster(cfg);

      JobExecutionResult res;
      try {
        miniCluster.start();
        configuration.setInteger(
            RestOptions.PORT,
            miniCluster.getRestAddress().get().getPort());
        res = miniCluster.executeJobBlocking(jobGraph);
      } finally {
        this.transformations.clear();
        miniCluster.close();
      }

      return res;
    }
  }
}
