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

package com.uber.athena.dsl.planner;

import com.uber.athena.dsl.planner.model.ModelVertex;

/**
 * Blackboard is an indexed holder for a specific type of node.
 *
 * <p>Blackboard is used to host intermediate as well as final node contents
 * for builders and convertlets to perform actions.
 *
 * @param <T> the type of node this blackboard is designed to host.
 */
public interface Blackboard<T> {

  /**
   * Retrieve node based on DSL model vertex.
   *
   * @param vertex the vertex model.
   * @return the corresponding node saved previously in the blackboard.
   */
  T getNode(ModelVertex vertex);

  /**
   * Save a node to the blackboard associating it with the DSL model vertex.
   *
   * @param node the node object.
   * @param vertex the vertex model.
   */
  void saveNode(T node, ModelVertex vertex);
}
