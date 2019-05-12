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

import com.uber.athena.flux.api.topology.FluxTopology;
import com.uber.athena.flux.model.TopologyDef;
import com.uber.athena.flux.parser.FluxParser;
import org.junit.Ignore;
import org.junit.Test;

public class TopologyRuntimeITCase {

  @Test
  public void testBasicTopologyRuntime() throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/basic_topology.yaml", false, true, null, false);
    topologyDef.validate();
    FluxTopologyBuilderImpl fluxBuilder = FluxTopologyBuilderImpl.createFluxBuilder();
    FluxTopology topology = fluxBuilder.createTopology(topologyDef, null);
    topology.execute();
  }

  @Test
  public void testRepartitionTopologyRuntime() throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/repartition_topology.yaml", false, true, null, false);
    topologyDef.validate();
    FluxTopologyBuilderImpl fluxBuilder = FluxTopologyBuilderImpl.createFluxBuilder();
    FluxTopology topology = fluxBuilder.createTopology(topologyDef, null);
    topology.execute();
  }

  @Test
  @Ignore("Need to implement kafka test base to launch mini-kafka")
  public void testKafkaTopologyRuntime() throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/kafka_topology.yaml", false, true, null, false);
    topologyDef.validate();
    FluxTopologyBuilderImpl fluxBuilder = FluxTopologyBuilderImpl.createFluxBuilder();
    FluxTopology topology = fluxBuilder.createTopology(topologyDef, null);
    topology.execute();
  }
}
