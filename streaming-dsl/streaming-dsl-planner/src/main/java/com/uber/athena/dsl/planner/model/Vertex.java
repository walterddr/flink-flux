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

package com.uber.athena.dsl.planner.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the {@link VertexNode}.
 *
 * <p>This node implementation represents a vertex within a
 * {@link com.uber.athena.dsl.planner.topology.Topology}.
 *
 * <p>If a more complex element or expression builder is present. One could
 * implement its own {@link VertexNode} in order to support more ambiguous
 * upstream/downstream connections or stream definitions.
 */
public class Vertex implements VertexNode, Serializable {

  protected final String vertexId;
  protected final VertexDef vertexDef;

  protected List<String> upstreamVertexIds;
  protected List<String> downstreamVertexIds;
  protected Map<String, StreamDef> upstreams;

  public Vertex(
      String vertexId,
      VertexDef vertexDef,
      List<String> upstreamVertexIds,
      List<String> downstreamVertexIds,
      Map<String, StreamDef> upstreams) {
    this.vertexId = vertexId;
    this.vertexDef = vertexDef;
    this.upstreamVertexIds = upstreamVertexIds;
    this.downstreamVertexIds = downstreamVertexIds;
    this.upstreams = upstreams;
  }

  @Override
  public String getVertexId() {
    return vertexDef.getId();
  }

  @Override
  public VertexDef getVertexDef() {
    return vertexDef;
  }

  @Override
  public List<String> getUpstreamVertexIds() {
    return upstreamVertexIds;
  }

  @Override
  public List<String> getDownstreamVertexIds() {
    return downstreamVertexIds;
  }

  @Override
  public Map<String, StreamDef> getUpstreams() {
    return upstreams;
  }

  @Override
  public StreamDef getUpstream(String upstreamId) {
    return upstreams.get(upstreamId);
  }
}
