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

package com.uber.athena.flux.api.topology;

import com.uber.athena.flux.model.TopologyDef;

import java.io.IOException;
import java.util.Map;

/**
 * Marker interface for objects that can produce `StormTopology` objects.
 *
 * <p>If a `topology-source` class implements the `createTopology()` method, Flux will
 * call that method.
 *
 * <p>A specific Flux execution framework should implement this interface and produce
 * concrete {@code FluxTopology} implementations that can be executed within the framework.
 */
public interface FluxTopologyBuilder {

  /**
   * Create Flux topology based on topology definition.
   *
   * @param topologyDef topology definition
   * @param config      configuration global map
   * @return topology
   */
  FluxTopology createTopology(TopologyDef topologyDef, Map<String, Object> config) throws IOException;
}
