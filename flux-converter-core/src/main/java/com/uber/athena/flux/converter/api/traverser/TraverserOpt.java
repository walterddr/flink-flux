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

package com.uber.athena.flux.converter.api.traverser;

import com.uber.athena.flux.model.StreamDef;
import com.uber.athena.flux.model.VertexDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Base traverser operand representing a node's topology characteristic.
 */
public class TraverserOpt {

  protected final String vertexId;
  protected final VertexDef vertexDef;

  protected List<String> upstreamVertexIds;
  protected List<String> downstreamVertexIds;
  protected List<StreamDef> upstreams;

  public TraverserOpt(
      String vertexId,
      VertexDef vertexDef) {
    this(vertexId, vertexDef, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  public TraverserOpt(
      String vertexId,
      VertexDef vertexDef,
      List<String> upstreamVertexIds,
      List<String> downstreamVertexIds,
      List<StreamDef> upstreams) {
    this.vertexId = vertexId;
    this.vertexDef = vertexDef;
    this.upstreamVertexIds = upstreamVertexIds;
    this.downstreamVertexIds = downstreamVertexIds;
    this.upstreams = upstreams;
  }

  public String getVertexId() {
    return vertexDef.getId();
  }

  public VertexDef getVertexDef() {
    return vertexDef;
  }

  public List<String> getUpstreamVertexIds() {
    return upstreamVertexIds;
  }

  public List<String> getDownstreamVertexIds() {
    return downstreamVertexIds;
  }

  public List<StreamDef> getUpstreams() {
    return upstreams;
  }

  public void addUpstreamVertexId(String upstreamVertexId) {
    this.upstreamVertexIds.add(upstreamVertexId);
  }

  public void addDownstreamVertexId(String upstreamVertexId) {
    this.downstreamVertexIds.add(upstreamVertexId);
  }

  public void addUpstream(StreamDef streamDef) {
    this.upstreams.add(streamDef);
  }
}
