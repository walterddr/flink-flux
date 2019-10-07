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

package com.uber.athena.dsl.planner.flink.relation;

import com.uber.athena.dsl.planner.flink.type.FlinkType;
import com.uber.athena.dsl.planner.model.StreamDef;
import com.uber.athena.dsl.planner.model.StreamSpecDef;
import com.uber.athena.dsl.planner.relation.RelationNode;
import com.uber.athena.dsl.planner.relation.rule.RuleCall;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The {@link RelationNode} for Flink Table applications.
 *
 * <p>The relation node construct holds information after a relation has been
 * successfully constructed.
 */
@SuppressWarnings("unchecked")
public class FlinkTableRelationNode implements RelationNode {

  private static final StreamDef DEFAULT_STREAM_DEF =
      new StreamDef()
          .streamSpec(new StreamSpecDef()
              .streamType(StreamSpecDef.StreamTypeEnum.DATA_STREAM));

  private final String vertexId;
  private final HashMap<String, StreamDef> upstreamDefMap;
  private final HashMap<String, RelationNode> upstreamNodeMap;
  private final HashMap<StreamDef, Object> downstreamRelationObjMap;
  private final HashMap<StreamDef, Class<?>> downstreamRelationClassMap;

  public FlinkTableRelationNode(
      RuleCall ruleCall,
      Object obj,
      Class<?> clazz) {
    this(ruleCall, obj, clazz, DEFAULT_STREAM_DEF);
  }

  public FlinkTableRelationNode(
      RuleCall ruleCall,
      Object obj,
      Class<?> clazz,
      StreamDef streamDef) {
    this.vertexId = ruleCall.getVertexId();
    this.upstreamDefMap = new HashMap<>(ruleCall.getUpstreamDefMapping());
    this.upstreamNodeMap = new HashMap<>(ruleCall.getUpstreamRelationMapping());
    this.downstreamRelationObjMap = new HashMap<>();
    this.downstreamRelationClassMap = new HashMap<>();
    downstreamRelationClassMap.put(streamDef, clazz);
    downstreamRelationObjMap.put(streamDef, obj);
  }

  @Override
  public String getVertexId() {
    return vertexId;
  }

  @Override
  public List<String> getUpstreamVertices() {
    return new ArrayList<>(upstreamDefMap.keySet());
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
    return downstreamRelationClassMap.get(streamDef);
  }

  @Override
  public <R> R getRelation(StreamDef streamDef) {
    return (R) downstreamRelationObjMap.get(streamDef);
  }

  @Override
  public FlinkType getProduceType(StreamDef streamDef) {
    TableSchema tableSchema = ((Table) this.getRelation(streamDef)).getSchema();
    return new FlinkType(tableSchema);
  }
}
