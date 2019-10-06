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

package com.uber.athena.dsl.planner.relation;

import com.uber.athena.dsl.planner.model.StreamSpecDef;
import com.uber.athena.dsl.planner.model.VertexNode;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.utils.TraverseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Traverse context for constructing relation based on {@link Topology}.
 */
public class RelationTraverseContext {
  private static final Logger LOG = LoggerFactory.getLogger(RelationTraverseContext.class);

  private final Topology topology;
  private final Map<String, Object> traverseConfig;

  private Map<String, VertexNode> nodeMap;
  private Map<String, StreamSpecDef> streamMap;
  private Map<String, TraverseState> stateMap;

  private Queue<String> traverseQueue;

  private boolean initialized;

  public RelationTraverseContext(Topology topology) {
    this(topology, null);
  }

  public RelationTraverseContext(Topology topologyDef, Map<String, Object> traverseConfig) {
    this.topology = topologyDef;
    this.traverseConfig = traverseConfig;
  }

  public void init() {
    // initialize context maps.
    nodeMap = new HashMap<>();
    streamMap = new HashMap<>();

    // Build the Traversing Graph
    for (VertexNode source : topology.getSources().values()) {
      nodeMap.put(source.getVertexId(), source);
    }
    for (VertexNode sink : topology.getSinks().values()) {
      nodeMap.put(sink.getVertexId(), sink);
      if (sink.getUpstreams() != null) {
        sink.getUpstreams().forEach(
            (k, v) -> streamMap.put(k, v.getStreamSpec())
        );
      }
    }
    for (VertexNode operator : topology.getOperators().values()) {
      nodeMap.put(operator.getVertexId(), operator);
      if (operator.getUpstreams() != null) {
        operator.getUpstreams().forEach(
            (k, v) -> streamMap.put(k, v.getStreamSpec())
        );
      }
    }

    // Initialize the traversing graph state
    traverseQueue = new PriorityQueue<>();
    stateMap = new HashMap<>();
    nodeMap.keySet().forEach(id -> stateMap.put(id, new TraverseState()));

    for (VertexNode source : topology.getSources().values()) {
      traverseQueue.add(source.getVertexId());
    }

    // Set initialized to true
    initialized = true;
  }

  public VertexNode getVertexNode(String vertexId) {
    return nodeMap.get(vertexId);
  }

  public boolean isInitialized() {
    return initialized;
  }

  public String visitNextVertexId() throws TraverseException {
    if (!this.isInitialized()) {
      throw new TraverseException("Unable to construct traversing context!");
    }

    // Return null if traverse queue is empty or null
    if (traverseQueue.peek() == null) {
      return null;
    }

    // Convert the node from head of the queue.
    String currentId = traverseQueue.poll();

    if (stateMap.get(currentId).visited) {
      LOG.warn("vertex {} has been visited previously!", currentId);
    }

    // Update state map.
    for (String toId : nodeMap.get(currentId).getDownstreamVertexIds()) {
      TraverseState state = stateMap.get(toId);
      state.visitedUpstream.add(currentId);
      if (state.visitedUpstream.size() == nodeMap.get(toId).getUpstreams().size()) {
        traverseQueue.add(toId);
      }
    }

    stateMap.get(currentId).visited = true;
    return currentId;
  }

  public boolean hasNextVertex() throws TraverseException {
    if (!this.isInitialized()) {
      throw new TraverseException("Unable to construct traversing context!");
    }
    return !traverseQueue.isEmpty();
  }

  private static class TraverseState {
    private boolean visited;
    private List<String> visitedUpstream;

    TraverseState() {
      visited = false;
      visitedUpstream = new ArrayList<>();
    }
  }
}
