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

package com.uber.athena.dsl.utils;

import com.uber.athena.dsl.model.ComponentDef;
import com.uber.athena.dsl.model.VertexDef;
import com.uber.athena.dsl.topology.api.Topology;
import com.uber.athena.dsl.topology.api.Vertex;

import java.util.Collection;
import java.util.Collections;

/**
 * Bean represenation of a topology.
 *
 * <p>It consists of the following:
 * 1. The topology name
 * 2. A `java.util.Map` representing the Flink {@code Configuration} for the topology
 * 3. A list of source definitions
 * 4. A list of operator definitions
 * 5. A list of sink definitions
 * 6. A list of stream definitions that define the flow between components.
 */
public final class TopologyUtils {

  private TopologyUtils() {
    // do not instantiate.
  }

  // utility methods
  public static int getVertexParallism(Topology topology, String id) {
    return ((VertexDef) topology.getComponents().get(id)).getParallelism();
  }

  public static Vertex getOperatorDef(Topology topology, String id) {
    return topology.getOperators().get(id);
  }

  public static Vertex getSourceDef(Topology topology, String id) {
    return topology.getSources().get(id);
  }

  public static Vertex getSinkDef(Topology topology, String id) {
    return topology.getSinks().get(id);
  }

  public ComponentDef getComponent(Topology topology, String id) {
    return topology.getComponents().get(id);
  }

  public static Collection<Vertex> getSourceList(Topology topology) {
    return topology.getSources() == null
        ? Collections.emptyList() : topology.getSources().values();
  }

  public static Collection<Vertex> getSinkList(Topology topology) {
    return topology.getSinks() == null
        ? Collections.emptyList() : topology.getSinks().values();
  }

  public static Collection<Vertex> getOperatorList(Topology topology) {
    return topology.getOperators() == null
        ? Collections.emptyList() : topology.getOperators().values();
  }
}
