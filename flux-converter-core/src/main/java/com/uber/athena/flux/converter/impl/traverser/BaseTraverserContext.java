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

package com.uber.athena.flux.converter.impl.traverser;

import com.uber.athena.flux.converter.api.traverser.TraverserContext;
import com.uber.athena.flux.converter.api.traverser.TraverserOpt;
import com.uber.athena.flux.model.OperatorDef;
import com.uber.athena.flux.model.SinkDef;
import com.uber.athena.flux.model.SourceDef;
import com.uber.athena.flux.model.StreamDef;
import com.uber.athena.flux.model.StreamSpecDef;
import com.uber.athena.flux.model.TopologyDef;
import com.uber.athena.flux.model.VertexDef;
import com.uber.athena.flux.utils.TopologyUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This is a basic traverse context implementation.
 *
 * <p>This traverse context only provides the nodes stored on the starting
 *
 */
public class BaseTraverserContext implements TraverserContext {

  private final TopologyDef topologyDef;
  private final Properties contextProperties;
  private final Map<String, TraverserOpt> traverseNodeMap;

  public BaseTraverserContext(
      TopologyDef topologyDef
  ) {
    this(topologyDef, null);
  }

  public BaseTraverserContext(
      TopologyDef topologyDef,
      Properties contextProperties
  ) {
    this(topologyDef, contextProperties, new HashMap<>());
  }

  BaseTraverserContext(
      TopologyDef topologyDef,
      Properties contextProperties,
      Map<String, TraverserOpt> traverseNodeMap
  ) {
    this.topologyDef = topologyDef;
    this.contextProperties = contextProperties;
    this.traverseNodeMap = traverseNodeMap;
    initializeContext();
  }

  public void initializeContext() {
    Map<String, StreamSpecDef> streamSpecDefMap = new HashMap<>();

    // ------------------------------------------------------------------------
    // Build the Compilation Graph
    // ------------------------------------------------------------------------

    // TODO(@walterddr) add Null checker as a validation.

    // Add all vertices.
    for (SourceDef sourceDef : TopologyUtils.getSourceList(topologyDef)) {
      traverseNodeMap.put(
          sourceDef.getId(),
          constructTraverserOpt(sourceDef.getId(), sourceDef));
    }
    for (SinkDef sinkDef : TopologyUtils.getSinkList(topologyDef)) {
      traverseNodeMap.put(
          sinkDef.getId(),
          constructTraverserOpt(sinkDef.getId(), sinkDef));
      if (sinkDef.getInputSpec() != null) {
        streamSpecDefMap.putAll(sinkDef.getInputSpec());
      }
    }
    for (OperatorDef operatorDef : TopologyUtils.getOperatorList(topologyDef)) {
      traverseNodeMap.put(
          operatorDef.getId(),
          constructTraverserOpt(operatorDef.getId(), operatorDef));
      if (operatorDef.getInputSpec() != null) {
        streamSpecDefMap.putAll(operatorDef.getInputSpec());
      }
    }

    // Add all edges.
    for (StreamDef streamDef : topologyDef.getStreams()) {
      StreamSpecDef spec = streamSpecDefMap.get(streamDef.getId());
      if (spec != null) {
        streamDef.setStreamSpec(spec);
      } else {
        streamDef.setStreamSpec(getDefaultStreamSpec());
      }
      TraverserOpt fromNode = traverseNodeMap.get(streamDef.getFromVertex());
      TraverserOpt toNode = traverseNodeMap.get(streamDef.getToVertex());
      if (fromNode == null || toNode == null) {
        throw new UnsupportedOperationException("Unsupported DSL graph. Cannot find proper vertex"
            + " definitions: " + streamDef.getFromVertex() + "-> " + streamDef.getToVertex());
      }
      toNode.addUpstreamVertexId(streamDef.getFromVertex());
      fromNode.addDownstreamVertexId(streamDef.getToVertex());
      toNode.addUpstream(streamDef);
    }
  }

  @Override
  public TopologyDef getTopologyDef() {
    return topologyDef;
  }

  @Override
  public TraverserOpt getTraverserOpt(String vertexId) {
    return traverseNodeMap.get(vertexId);
  }

  public Collection<TraverserOpt> getAllTraverseNodes() {
    return traverseNodeMap.values();
  }

  public Properties getContextProperties() {
    return contextProperties;
  }

  private static StreamSpecDef getDefaultStreamSpec() {
    StreamSpecDef defaultSpec = new StreamSpecDef();
    defaultSpec.setStreamType(StreamSpecDef.StreamTypeEnum.DATA_STREAM);
    return defaultSpec;
  }

  private static TraverserOpt constructTraverserOpt(String vertexId, VertexDef vertexDef) {
    return new TraverserOpt(vertexId, vertexDef);
  }
}
