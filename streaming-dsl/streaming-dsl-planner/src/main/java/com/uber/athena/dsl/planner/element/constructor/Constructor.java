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

package com.uber.athena.dsl.planner.element.constructor;

import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.model.VertexNode;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.utils.ConstructionException;

/**
 * Base class for construction of vertex.
 */
public interface Constructor {

  /**
   * Construct an object based on topology and vertex definitions.
   *
   * @param vertex the vertex node defined in the topology
   * @param topology the topology.
   * @return a Java object constructed.
   * @throws ConstructionException when construction failure occurs.
   */
  ElementNode construct(VertexNode vertex, Topology topology) throws ConstructionException;
}
