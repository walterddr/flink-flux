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
import com.uber.athena.dsl.planner.model.VertexNode;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.utils.ConstructionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
  private Constructor constructor;

  /**
   * Configuration properties.
   */
  private Properties config;

  /**
   * Construct an element builder.
   *
   * @param builderConfig configuration properties for the builder.
   * @param constructor the constructor used to construct elements.
   */
  public ElementBuilder(
      Properties builderConfig,
      Constructor constructor) {
    this.config = builderConfig;
    this.constructor = constructor;
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
  public Map<String, ElementNode> construct(
      Topology topology) throws ConstructionException {
    Map<String, ElementNode> elementMapping = new HashMap<>();
    for (VertexNode vertex : topology.getSources().values()) {
      ElementNode node = constructor.construct(vertex, topology);
      elementMapping.put(vertex.getVertexId(), node);
    }
    for (VertexNode vertex : topology.getSinks().values()) {
      ElementNode node = constructor.construct(vertex, topology);
      elementMapping.put(vertex.getVertexId(), node);
    }
    for (VertexNode vertex : topology.getOperators().values()) {
      ElementNode node = constructor.construct(vertex, topology);
      elementMapping.put(vertex.getVertexId(), node);
    }
    return elementMapping;
  }

  public Properties getConfig() {
    return config;
  }
}
