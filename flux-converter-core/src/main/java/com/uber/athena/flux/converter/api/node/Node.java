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

import java.io.Serializable;
import java.util.List;

/**
 * Base interface for a converter {@code Node}.
 */
public interface Node extends Serializable {

  /**
   * Retruns the type of object this node is capable of constructing.
   * @return Clazz of the node object.
   */
  Class<?> getObjectClass();

  /**
   * Returns the vertex Id associated with this Node.
   * @return vertex Id
   */
  String getVertexId();

  /**
   * Returns the vertex definition in its original form in the DSL.
   * @return vertex definition
   */
  VertexDef getVertexDef();

  /**
   * Returns all upstream vertex Id linked with this node.
   * @return upstream vertex Id list.
   */
  List<String> getUpstreamVertexIds();

  /**
   * Returns all downstream vertex Id linked with this node.
   * @return downstream vertex Id list.
   */
  List<String> getDownstreamVertexIds();

  /**
   * Returns all upstream definitions of the linkage.
   * @return list of stream definitions
   */
  List<StreamDef> getUpstreams();

  /**
   * Linked another upstream vertex with the current node.
   * @param upstreamVertexId upstream vertex Id to be linked.
   */
  void addUpstreamVertexId(String upstreamVertexId);

  /**
   * Linked another downstream vertex with the current node.
   * @param upstreamVertexId downstream vertex Id to be linked.
   */
  void addDownstreamVertexId(String upstreamVertexId);

  /**
   * Add another upstream definition to this node.
   * @param stream definition of the upstream linkage.
   */
  void addUpstream(StreamDef stream);
}
