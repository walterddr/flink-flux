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

package com.uber.athena.dsl.topology.model;

import com.uber.athena.dsl.model.StreamDef;
import com.uber.athena.dsl.model.VertexDef;
import com.uber.athena.dsl.topology.api.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Specific impl of {@link Vertex} in DSL context.
 */
public class DslVertex extends Vertex {

  public DslVertex(
      String vertexId,
      VertexDef vertexDef) {
    super(vertexId, vertexDef, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  public void addUpstreamVertices(List<String> upstream) {
    if (upstream != null) {
      this.upstreamVertexIds.addAll(upstream);
    }
  }

  public void addDownstreamVertices(List<String> downstream) {
    if (downstream != null) {
      this.downstreamVertexIds.addAll(downstream);
    }
  }

  public void addStreams(List<StreamDef> streams) {
    if (streams != null) {
      this.upstreams.addAll(streams);
    }
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
