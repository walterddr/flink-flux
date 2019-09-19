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

import com.uber.athena.dsl.planner.Blackboard;
import com.uber.athena.dsl.planner.element.constructor.Constructor;
import com.uber.athena.dsl.planner.element.convertlet.ConvertletExecutor;
import com.uber.athena.dsl.planner.model.ModelVertex;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.type.Type;
import com.uber.athena.dsl.planner.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Builder for constructing an {@link Element} from a {@link ModelVertex}.
 *
 * <p>Element builder is used to construct a proper Object for later
 * relation construction. It does process some
 */
public class ElementBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(ElementBuilder.class);

  /**
   * Element constructor that creates an object from class reference.
   */
  private Constructor constructor;

  /**
   * Configuration properties for {@link Convertlet}s and {@link TypeFactory}.
   */
  private Properties config;

  /**
   * Type/Schema factory used to determine output schema of an Element.
   */
  private TypeFactory typeFactory;

  /**
   * Executor of {@link Convertlet}s.
   *
   * <p>{@link Convertlet}s are encapsulated within the executor environment.
   */
  private ConvertletExecutor convertlet;

  /**
   * Construct an element builder.
   *
   * @param builderConfig configuration properties for the builder.
   * @param typeFactory the type factory to construct element produce type.
   * @param convertlet the optional convertlet used to convert element.
   */
  public ElementBuilder(
      Properties builderConfig,
      Constructor constructor,
      TypeFactory typeFactory,
      ConvertletExecutor convertlet) {
    this.config = builderConfig;
    this.constructor = constructor;
    this.typeFactory = typeFactory;
    this.convertlet = convertlet;
  }

  /**
   * Construct ElementNodes for the entire topology.
   *
   * <p>Constructed nodes will be put into the {@link Blackboard} provided.
   *
   * @param topology the topology defined by the DSL model.
   * @param elementBlackboard blackboard for holding constructed elements.
   */
  public void construct(
      Topology topology,
      Blackboard<ElementNode> elementBlackboard) {
    for (ModelVertex vertex : topology.getSources().values()) {
      ElementNode node = construct(vertex, topology);
      elementBlackboard.saveNode(node, vertex);
    }
    for (ModelVertex vertex : topology.getSinks().values()) {
      ElementNode node = construct(vertex, topology);
      elementBlackboard.saveNode(node, vertex);
    }
    for (ModelVertex vertex : topology.getOperators().values()) {
      ElementNode node = construct(vertex, topology);
      elementBlackboard.saveNode(node, vertex);
    }
  }

  private ElementNode construct(ModelVertex vertex, Topology topology) {
    try {
      // Construct the element from vertex using reflection.
      Object obj = constructor.construct(vertex.getVertexDef(), topology);
      Class<?> clazz = Class.forName(vertex.getVertexDef().getClassName());

      // Determines the constructed element produce type.
      Type type = typeFactory.getType(vertex);
      ElementNode node = new Element(obj, clazz, type);

      // Invoke convertlet executor to make extended conversions.
      // TODO @walterddr add convertlet invocation.

      return node;
    } catch (Exception e) {
      LOG.error("Cannot construct element!", e);
      return null;
    }
  }

  public ConvertletExecutor getConvertlet() {
    return convertlet;
  }

  public TypeFactory getTypeFactory() {
    return typeFactory;
  }

  public Properties getConfig() {
    return config;
  }
}
