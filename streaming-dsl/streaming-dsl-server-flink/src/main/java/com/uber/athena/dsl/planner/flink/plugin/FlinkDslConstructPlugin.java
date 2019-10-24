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

import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.relation.RelationNode;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.utils.ConstructionException;
import com.uber.athena.plugin.api.PluginResult;
import org.apache.flink.runtime.jobgraph.JobGraph;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Plugin returns the DSL constructed job graph as {@link PluginResult}.
 */
public class FlinkDslConstructPlugin extends FlinkPlugin {

  @Override
  public PluginResult run() throws Exception {
    String filename = payload.getInputFile();
    try (InputStream file = new FileInputStream(filename)) {
      Topology topology = planner.parse(file);
      topology = planner.validate(topology);
      Map<String, ElementNode> elementMapping =
          planner.constructElement(topology);
      Map<String, ? extends RelationNode> relationMapping =
          planner.constructRelation(topology, elementMapping);
      if (relationMapping.size() != elementMapping.size()) {
        throw new ConstructionException("Unmatched relation generation occurred! "
            + "Expected: " + elementMapping.size() + " but found: " + relationMapping.size());
      }
      JobGraph jobGraph = ruleSet.getStreamExecutionEnvironment().getStreamGraph().getJobGraph();
      return new FlinkPluginResult(jobGraph);
    }
  }
}
