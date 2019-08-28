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

package com.uber.athena.dsl.topology.validate;

import com.uber.athena.dsl.model.TopologyDef;
import com.uber.athena.dsl.topology.api.Topology;
import com.uber.athena.dsl.topology.exceptions.ConstructionException;
import com.uber.athena.dsl.topology.exceptions.ValidationException;
import com.uber.athena.dsl.topology.model.DslTopologyBuilder;

import java.util.Map;

/**
 * Check if a {@link TopologyDef} is valid and can represent a topology DAG.
 */
public class DslValidator {
  private DslTopologyBuilder topologyBuilder;

  public DslValidator(DslTopologyBuilder topologyBuilder) {
    this.topologyBuilder = topologyBuilder;
  }

  /**
   * validate that the definition actually represents a valid DSL topology.
   * @return true if the topology def is valid.
   */
  public void validate(
      TopologyDef topologyDef,
      Map<String, Object> config) throws ValidationException {
    try {
      Topology topology = topologyBuilder.createTopology(topologyDef, config);
      validateTopology(topology);
    } catch (ConstructionException e) {
      throw new ValidationException("Validation Exception occur, cannot construct topology", e);
    }
  }

  /**
   * validate that the definition actually represents a valid DSL topology.
   * @return true if the topology def is valid.
   */
  public void validate(Topology topology) throws ValidationException {
    try {
      validateTopology(topology);
    } catch (Exception e) {
      throw new ValidationException("Validation Exception occur!", e);
    }
  }

  /**
   * validate that the definition actually represents a valid DSL topology.
   * @return true if the topology def is valid.
   */
  private static void validateTopology(Topology topology) throws ValidationException {
    if (topology.getSources() == null || topology.getSources().size() == 0) {
      throw new ValidationException("Validation Exception: No source vertex found!");
    }
    if (topology.getSinks() == null || topology.getSinks().size() == 0) {
      throw new ValidationException("Validation Exception: No sink vertex found!");
    }
    if (topology.getStreams() == null || topology.getStreams().size() == 0) {
      throw new ValidationException("Validation Exception: No stream edge found!");
    }
  }
}
