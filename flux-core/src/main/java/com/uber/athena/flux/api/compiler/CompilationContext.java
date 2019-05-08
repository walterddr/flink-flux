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

package com.uber.athena.flux.api.compiler;

import com.uber.athena.flux.model.TopologyDef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompilationContext {

  // compilation environment configurations
  private CompilationConfig config;

  // parsed Topology definition
  private TopologyDef topologyDef;

  // components required to be instantiated from classpath JARs
  private List<Object> additionalComponents;

  /**
   * The following are materialized objects from the {@link TopologyDef}.
   */
  private Map<String, Object> componentMap = new HashMap<String, Object>();

  private Map<String, SourceVertex<?>> sourceMap = new HashMap<String, SourceVertex<?>>();

  private Map<String, OperatorVertex<?>> operatorMap = new HashMap<String, OperatorVertex<?>>();

  private Map<String, SinkVertex<?>> sinkMap = new HashMap<String, SinkVertex<?>>();

  private Map<String, CompilationVertex> compilationVertexMap = new HashMap<>();

  public CompilationContext(TopologyDef topologyDef, CompilationConfig config) {
    this.topologyDef = topologyDef;
    this.config = config;
  }

  public TopologyDef getTopologyDef() {
    return this.topologyDef;
  }

  public List<Object> getAdditionalComponents() {
    return additionalComponents;
  }

  public void setAdditionalComponents(List<Object> additionalComponents) {
    this.additionalComponents = additionalComponents;
  }

  public CompilationConfig getConfig() {
    return config;
  }

  public void setConfig(CompilationConfig config) {
    this.config = config;
  }

  /**
   * add source.
   *
   * @param id source id
   * @param source source object
   */
  public void addSource(String id, SourceVertex<?> source) {
    this.sourceMap.put(id, source);
  }

  /**
   * Add sink.
   *
   * @param id sink ID
   * @param sink sink object
   */
  public void addSink(String id, SinkVertex<?> sink) {
    this.sinkMap.put(id, sink);
  }

  /**
   * add operator.
   */
  public void addOperator(String id, OperatorVertex<?> op) {
    this.operatorMap.put(id, op);
  }

  /**
   * add compilation components, used for reference.
   * @param id component key
   * @param value component object
   */
  public void addComponent(String id, Object value) {
    this.componentMap.put(id, value);
  }

  /**
   * get component by ID.
   *
   * @param id component id
   * @return the component as object
   */
  public Object getComponent(String id) {
    return this.componentMap.get(id);
  }

  /**
   * Put a compilation vertex into the vertex map.
   *
   * @param key   vertex id, identical to the ComponentDef ID
   * @param value compilation vertex.
   */
  public void putCompilationVertex(String key, CompilationVertex value) {
    compilationVertexMap.put(key, value);
  }

  /**
   * get a compilation vertex by ID.
   *
   * @param key vertex id, identical to the ComponentDef ID
   * @return compilation vertex.
   */
  public CompilationVertex getCompilationVertex(String key) {
    return compilationVertexMap.get(key);
  }
}
