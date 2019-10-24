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

import com.uber.athena.plugin.api.PluginResult;
import org.apache.flink.runtime.jobgraph.JobGraph;

import static com.uber.athena.plugin.utils.SerializationUtils.javaDeserialize;
import static com.uber.athena.plugin.utils.SerializationUtils.serializerJavaObj;

/**
 * {@link PluginResult} for Flink DSL construct.
 */
public class FlinkPluginResult implements PluginResult<FlinkPluginResult> {
  private JobGraph jobGraph;
  private Throwable exception;

  public FlinkPluginResult(JobGraph jobGraph) {
    this.jobGraph = jobGraph;
  }

  @Override
  public byte[] serialize() throws Exception {
    return serializerJavaObj(this);
  }

  @Override
  public FlinkPluginResult deserialize(byte[] serializedObj) throws Exception {
    return javaDeserialize(serializedObj);
  }

  @Override
  public void setException(Throwable e) {
    this.exception = e;
  }

  @Override
  public Throwable getException() {
    return exception;
  }

  public JobGraph getJobGraph() {
    return jobGraph;
  }
}
