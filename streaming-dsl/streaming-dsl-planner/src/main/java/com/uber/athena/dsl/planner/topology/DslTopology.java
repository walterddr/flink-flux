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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link Topology} implementation that represents an execution DAG based
 * on DSL models.
 */
public class DslTopology implements Topology {

  private final String name;
  private final Map<String, ComponentDef> components;
  private final HashMap<String, VertexNode> vertices;
  private final Map<String, VertexNode> sources;
  private final Map<String, VertexNode> sinks;
  private final Map<String, VertexNode> operators;
  private final List<StreamDef> streams;
  private final List<String> dependencies;
  private final Map<String, Object> config;
  private final Map<String, PropertyDef> propertyMap;

  public DslTopology(String name) {
    this.name = name;
    this.components = new HashMap<>();

    this.vertices = new HashMap<>();
    this.sources = new HashMap<>();
    this.sinks = new HashMap<>();
    this.operators = new HashMap<>();
    this.streams = new ArrayList<>();

    this.dependencies = new ArrayList<>();
    this.config = new HashMap<>();
    this.propertyMap = new HashMap<>();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Map<String, Object> getConfig() {
    return config;
  }

  @Override
  public List<String> getDependencies() {
    return dependencies;
  }

  @Override
  public Map<String, PropertyDef> getPropertyMap() {
    return propertyMap;
  }

  @Override
  public Map<String, ComponentDef> getComponents() {
    return components;
  }

  @Override
  public VertexNode getVertex(String vertexId) {
    return vertices.get(vertexId);
  }

  public void putVertex(String vertexId, VertexNode vertex) {
    this.vertices.put(vertexId, vertex);
  }

  @Override
  public Map<String, VertexNode> getOperators() {
    return operators;
  }

  @Override
  public Map<String, VertexNode> getSources() {
    return sources;
  }

  @Override
  public Map<String, VertexNode> getSinks() {
    return sinks;
  }

  @Override
  public List<StreamDef> getStreams() {
    return streams;
  }
}
