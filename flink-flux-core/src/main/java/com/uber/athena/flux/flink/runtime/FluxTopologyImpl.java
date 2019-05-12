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
 */

package com.uber.athena.flux.flink.runtime;

import com.uber.athena.flux.api.topology.FluxExecutionResult;
import com.uber.athena.flux.api.topology.FluxTopology;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

/**
 * implementation of the Flux Topology that is represented by Flink job graph.
 *
 * <p>This implementation encloses the {@link JobGraph} and the additional classpath jar files
 * in the construction: job graph represents the compiled Flink topology logic;
 * and the jar path(s) represents the additional user jar dependencies required for
 * execution.
 */
public class FluxTopologyImpl implements FluxTopology {

  private transient StreamExecutionEnvironment senv;
  private transient JobGraph jobGraph;
  private transient List<Path> additionalJars;

  public FluxTopologyImpl(StreamExecutionEnvironment senv) {
    this.senv = senv;
  }

  public List<Path> getAdditionalJars() {
    return additionalJars;
  }

  public void setAdditionalJars(List<Path> additionalJars) {
    this.additionalJars = additionalJars;
  }

  public JobGraph getJobGraph() {
    return jobGraph;
  }

  public void setJobGraph(JobGraph jobGraph) {
    this.jobGraph = jobGraph;
  }



  /**
   * Execute the topology.
   *
   * <p>Execution will happen in the current defined stream execution environment.
   *
   * @return execution results.
   * @throws Exception when execution fails.
   */
  @Override
  public FluxExecutionResult execute() throws Exception {
    return new FluxExecutionResultImpl(senv.execute());
  }
}



