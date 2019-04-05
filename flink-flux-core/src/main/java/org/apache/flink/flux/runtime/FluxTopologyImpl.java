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

package org.apache.flink.flux.runtime;

import org.apache.flink.core.fs.Path;
import org.apache.flink.flux.api.FluxTopology;
import org.apache.flink.runtime.jobgraph.JobGraph;

import java.util.List;

/**
 * implementation of the Flux Topology, encloses the job graph and the classpath jar files.
 */
public class FluxTopologyImpl implements FluxTopology {

  private transient JobGraph jobGraph;
  private transient List<Path> additionalJars;

  public FluxTopologyImpl() {
  }

  @Override
  public List<Path> getAdditionalJars() {
    return additionalJars;
  }

  public void setAdditionalJars(List<Path> additionalJars) {
    this.additionalJars = additionalJars;
  }

  @Override
  public JobGraph getJobGraph() {
    return jobGraph;
  }

  public void setJobGraph(JobGraph jobGraph) {
    this.jobGraph = jobGraph;
  }
}



