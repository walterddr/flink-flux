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

import com.uber.athena.dsl.planner.model.EdgeDef;
import com.uber.athena.dsl.planner.model.StreamDef;
import com.uber.athena.dsl.planner.type.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base relation node that computes a digest.
 */
@SuppressWarnings("unchecked")
public class BaseRelationNode implements RelationNode {

  protected String vertexId;
  protected Object obj;
  protected Class<?> clazz;

  protected Map<String, StreamDef> upstreamDefMap;
  protected Map<String, RelationNode> upstreamNodeMap;

  public BaseRelationNode(String id, Object obj, Class<?> clazz) {
    this(id, obj, clazz, new HashMap<>(), new HashMap<>());
  }

  public BaseRelationNode(
      String id,
      Object obj,
      Class<?> clazz,
      Map<String, StreamDef> upstreamDefMap,
      Map<String, RelationNode> upstreamNodeMap) {
    this.vertexId = id;
    this.obj = obj;
    this.clazz = clazz;
    this.upstreamDefMap = upstreamDefMap;
    this.upstreamNodeMap = upstreamNodeMap;
  }

  @Override
  public String getVertexId() {
    return vertexId;
  }

  @Override
  public List<String> getUpstreamVertices() {
    return upstreamDefMap.values()
        .stream()
        .map(EdgeDef::getFromVertex)
        .collect(Collectors.toList());
  }

  @Override
  public RelationNode getUpstreamNode(String vertexId) {
    return upstreamNodeMap.get(vertexId);
  }

  @Override
  public StreamDef getUpstreamDef(String vertexId) {
    return upstreamDefMap.get(vertexId);
  }

  @Override
  public Class<?> getRelationClass(StreamDef streamDef) {
    return clazz;
  }

  @Override
  public <R> R getRelation(StreamDef streamDef) {
    return (R) obj;
  }

  @Override
  public <T extends Type> T getProduceType(StreamDef streamDef) {
    throw new UnsupportedOperationException("test relation node does not support this operation!");
  }
}
