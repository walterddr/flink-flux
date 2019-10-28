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

package com.uber.athena.dsl.planner.element;

import com.uber.athena.dsl.planner.element.constructor.Constructor;
import com.uber.athena.dsl.planner.model.ConfigMethodDef;
import com.uber.athena.dsl.planner.model.TypeSpecDef;
import com.uber.athena.dsl.planner.model.VertexDef;
import com.uber.athena.dsl.planner.model.VertexNode;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.type.TypeFactory;
import com.uber.athena.dsl.planner.utils.ConstructionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder for constructing an {@link Element} from a {@link VertexNode}.
 *
 * <p>Element builder is used to construct a proper Object for later
 * relation construction. It does process some
 */
public class ElementBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(ElementBuilder.class);

  /**
   * Element constructor that creates an object from class reference.
   */
  protected Constructor constructor;

  /**
   * Configuration properties.
   */
  protected Map<String, Object> config;

  /**
   * Type/Schema factory used to determine output schema of an Element.
   */
  protected TypeFactory typeFactory;

  /**
   * The indexed element reference mapping used for de-referencing.
   */
  protected Map<String, Object> referenceMap;

  /**
   * Construct an element builder.
   *
   * @param builderConfig configuration properties for the builder.
   * @param constructor the constructor used to construct elements.
   * @param typeFactory the type factory used to determine element produce type.
   */
  public ElementBuilder(
      Map<String, Object> builderConfig,
      Constructor constructor,
      TypeFactory typeFactory) {
    this(builderConfig, constructor, typeFactory, new HashMap<>());
  }

  ElementBuilder(
      Map<String, Object> builderConfig,
      Constructor constructor,
      TypeFactory typeFactory,
      Map<String, Object> referenceMap) {
    this.config = builderConfig;
    this.constructor = constructor;
    this.typeFactory = typeFactory;
    this.referenceMap = referenceMap;
  }

  /**
   * Construct {@link ElementNode}s for the topology.
   *
   * <p>Element builder invokes the constructor to create runtime objects for
   * the DSL topology. It provides a reflective constructor mechanism as
   * default approach.
   *
   * @param topology the topology defined by the DSL model.
   * @return a mapping from the vertex Ids to the constructed runtime objects.
   */
  @SuppressWarnings("unchecked")
  public <T extends ElementNode> Map<String, T> construct(
      Topology topology) throws ConstructionException {
    Map<String, ElementNode> elementMapping = new HashMap<>();
    for (VertexNode vertex : topology.getSources().values()) {
      elementMapping.put(
          vertex.getVertexId(),
          constructElementNode(vertex, topology));
    }
    for (VertexNode vertex : topology.getSinks().values()) {
      elementMapping.put(
          vertex.getVertexId(),
          constructElementNode(vertex, topology));
    }
    for (VertexNode vertex : topology.getOperators().values()) {
      elementMapping.put(
          vertex.getVertexId(),
          constructElementNode(vertex, topology));
    }
    return (Map<String, T>) elementMapping;
  }

  public Map<String, Object> getConfig() {
    return config;
  }

  protected ElementNode constructElementNode(
      VertexNode vertex,
      Topology topology) throws ConstructionException {
    LOG.debug("Element node construction successful for: " + vertex.getVertexId());

    VertexDef vertexDef = vertex.getVertexDef();

    // 1. Resolve type specifications & construct the produce type of the vertex,
    TypeSpecDef resolveTypeSpecDef = ComponentResolutionUtils.resolveTypeSpecDef(
        vertexDef.getTypeSpec());
    vertexDef.setTypeSpec(resolveTypeSpecDef);

    // 2. Resolve references
    List<Object> resolvedConstructorArgs = ComponentResolutionUtils.resolveReferences(
        vertexDef.getConstructorArgs(), topology, referenceMap);
    if (resolvedConstructorArgs != null) {
      vertexDef.setConstructorArgs(resolvedConstructorArgs);
    }

    // 3. Attach system-wide ConfigMethodDefs to the VertexNode
    List<ConfigMethodDef> configMethods = ComponentResolutionUtils.resolveConfigMethods(
        vertexDef, vertexDef.getConfigMethods());
    vertexDef.setConfigMethods(configMethods);

    // 4. Invoke constructor to construct the actual object.
    return constructor.construct(vertex, topology, typeFactory, referenceMap);
  }
}
