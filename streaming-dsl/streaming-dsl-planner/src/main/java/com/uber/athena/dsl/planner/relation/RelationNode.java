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

import com.uber.athena.dsl.planner.model.StreamDef;
import com.uber.athena.dsl.planner.type.Type;

import java.util.List;

/**
 * Node that represents the constructed DAG transformation for this vertex.
 *
 * <p>{@code RelationNode} serves as a planner result constructed from
 * source(s) of this DAG until this specific vertex.
 *
 * <p>The planner result can change depending on the {@link StreamDef}.
 *
 * <p>The constructed result depends on the actual runtime environment
 * supported. E.g. for Apache Flink, the constructed result can be in the form
 * of DataStream, DataSet or Table.
 */
public interface RelationNode {

  //---------------------------------------------------------------------------
  // Relation API associated with current Vertex
  //---------------------------------------------------------------------------

  /**
   * Return the vertexId associated with this relation node.
   *
   * <p>The vertex id is the one that's originally defined in the DSL topology
   * model.
   *
   * @return the vertex id as a string.
   */
  String getVertexId();

  //---------------------------------------------------------------------------
  // Relation API associated with upstreams
  //---------------------------------------------------------------------------

  /**
   * Return all the upstream vertex IDs.
   *
   * @return all the vertex IDs associated with this node as upstreams.
   */
  List<String> getUpstreamVertices();

  /**
   * Return the {@link RelationNode} for a particular upstream ID.
   *
   * @param vertexId the upstream ID.
   * @return the connected relation node.
   */
  RelationNode getUpstreamNode(String vertexId);

  /**
   * Return the {@link StreamDef} for a particular upstream ID.
   *
   * @param vertexId the upstream ID.
   * @return the connected stream definition.
   */
  StreamDef getUpstreamDef(String vertexId);

  //---------------------------------------------------------------------------
  // Relation API associated with downstreams
  //---------------------------------------------------------------------------

  /**
   * Return the type of relation this node constructs.
   *
   * @param streamDef the specification the downstream transformation.
   * @return Clazz of the relation object.
   */
  Class<?> getRelationClass(StreamDef streamDef);

  /**
   * Return the constructed relation object.
   *
   * <p>return relation type should match relation class acquired from the
   * {@code RelationNode.getRelationClass} method.
   *
   * @param <R> the relation result's class type.
   * @param streamDef the specification the downstream transformation.
   * @return the constructed relation object.
   */
  <R> R getRelation(StreamDef streamDef);

  /**
   * Return the output type of which the relation will produce.
   *
   * @param <T> the produce type result's class type.
   * @return the produce/output type of the element constructed.
   */
  <T extends Type> T getProduceType(StreamDef streamDef);
}
