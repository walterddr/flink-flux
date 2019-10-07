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

package com.uber.athena.dsl.planner.flink;

import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.flink.relation.rules.datastream.FlinkDataStreamRuleSet;
import com.uber.athena.dsl.planner.relation.RelationNode;
import com.uber.athena.dsl.planner.relation.rule.RuleSet;
import com.uber.athena.dsl.planner.topology.Topology;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;

/**
 * Test of the generation of Flink runtime job graph / application.
 */
public class PlannerITCase extends FlinkPlannerTestBase {
  private static final String BASIC_TOPOLOGY = "/dsl/basic_topology.yaml";
  private static final String DIAMOND_TOPOLOGY = "/dsl/diamond_topology.yaml";

  @Test
  @SuppressWarnings("unchecked")
  public void testBasicTopology() throws Exception {
    Configuration flinkConf = new Configuration();
    Properties properties = new Properties();
    StreamExecutionEnvironment sEnv =
        StreamExecutionEnvironment.createLocalEnvironment(1, flinkConf);
    RuleSet ruleSet = new FlinkDataStreamRuleSet(1, flinkConf, sEnv);
    FlinkPlanner planner = new FlinkPlanner.Builder()
        .flinkConf(flinkConf)
        .properties(properties)
        .ruleSet(ruleSet)
        .build();
    Topology topology = planner.parse(PlannerITCase.class.getResourceAsStream(BASIC_TOPOLOGY));
    topology = planner.validate(topology);
    Map<String, ElementNode> elementMapping =
        (Map<String, ElementNode>) planner.constructElement(topology);
    Map<String, ? extends RelationNode> relationMapping =
        planner.constructRelation(topology, elementMapping);

    sEnv.execute();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testDiamondTopology() throws Exception {
    Configuration flinkConf = new Configuration();
    Properties properties = new Properties();
    StreamExecutionEnvironment sEnv =
        StreamExecutionEnvironment.createLocalEnvironment(1, flinkConf);
    RuleSet ruleSet = new FlinkDataStreamRuleSet(1, flinkConf, sEnv);
    FlinkPlanner planner = new FlinkPlanner.Builder()
        .flinkConf(flinkConf)
        .properties(properties)
        .ruleSet(ruleSet)
        .build();
    Topology topology = planner.parse(PlannerITCase.class.getResourceAsStream(DIAMOND_TOPOLOGY));
    topology = planner.validate(topology);
    Map<String, ElementNode> elementMapping =
        (Map<String, ElementNode>) planner.constructElement(topology);
    Map<String, ? extends RelationNode> relationMapping =
        planner.constructRelation(topology, elementMapping);

    sEnv.execute();
  }

}
