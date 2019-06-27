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

package com.uber.athena.flux.converter.api.node;

import com.uber.athena.flux.model.StreamDef;
import com.uber.athena.flux.model.VertexDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Base node object that represents a node with its topology characteristic.
 *
 * <p>All Nodes should extend from this {@code BaseNode} and implements its
 * specific Node interface for that node conversion level:
 *
 * <p><ul>
 * <li>{@link com.uber.athena.flux.converter.api.node.dsl.DslNode}
 * <li>{@link com.uber.athena.flux.converter.api.node.element.ElementNode}
 * <li>... other user defined extensions
 * </ul></p>
 */
public class BaseNode implements Node {

  protected final String vertexId;
  protected final VertexDef vertexDef;

  protected final List<String> upstreamVertexIds;
  protected final List<String> downstreamVertexIds;
  protected final List<StreamDef> upstreams;

  public BaseNode(
      String vertexId,
      VertexDef vertexDef) {
    this(vertexId, vertexDef, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  public BaseNode(
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
  public List<StreamDef> getUpstreams() {
    return upstreams;
  }

  @Override
  public void addUpstreamVertexId(String upstreamVertexId) {
    this.upstreamVertexIds.add(upstreamVertexId);
  }

  @Override
  public void addDownstreamVertexId(String upstreamVertexId) {
    this.downstreamVertexIds.add(upstreamVertexId);
  }

  @Override
  public void addUpstream(StreamDef stream) {
    this.upstreams.add(stream);
  }
}
