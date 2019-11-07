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

package com.uber.athena.dsl.planner.topology;

import com.uber.athena.dsl.planner.model.ComponentDef;
import com.uber.athena.dsl.planner.model.PropertyDef;
import com.uber.athena.dsl.planner.model.StreamDef;
import com.uber.athena.dsl.planner.model.VertexNode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Dsl topology that can be converted into Execution job graph.
 */
public interface Topology extends Serializable {

  String getName();

  Map<String, Object> getConfig();

  List<String> getDependencies();

  Map<String, PropertyDef> getPropertyMap();

  Map<String, ComponentDef> getComponents();

  VertexNode getVertex(String vertexId);

  Map<String, VertexNode> getOperators();

  Map<String, VertexNode> getSources();

  Map<String, VertexNode> getSinks();

  List<StreamDef> getStreams();
}
