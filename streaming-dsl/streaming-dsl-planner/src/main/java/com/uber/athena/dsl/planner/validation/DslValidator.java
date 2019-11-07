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

package com.uber.athena.dsl.planner.validation;

import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.utils.ValidationException;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Basic implementation of the {@link Validator} check.
 */
public class DslValidator implements Validator {
  private static final Pattern ARTIFACT_DEPENDENCY_REGEX = Pattern.compile(
      "([-_\\w\\d\\.]+):([-_\\w\\d\\.]+):([\\d\\.]+)(\\^([-_\\w\\d\\.]+):([-_\\w\\d\\.]+))*");

  public DslValidator() {
  }

  @Override
  public Topology validate(Topology topology) throws ValidationException {
    try {
      validateTopology(topology);
      return topology;
    } catch (Exception e) {
      throw new ValidationException("Validation Exception occur!", e);
    }
  }

  /**
   * validate that the definition actually represents a valid DSL topology.
   */
  private static void validateTopology(Topology topology) throws ValidationException {
    if (topology.getDependencies() != null) {
      List<String> invalidDep = topology.getDependencies().stream()
          .filter(d -> !ARTIFACT_DEPENDENCY_REGEX.matcher(d).matches())
          .collect(Collectors.toList());
      if (invalidDep.size() > 0) {
        throw new ValidationException("Validation Exception: "
            + "Invalid artifact dependency configs: \n"
            + String.join("\n", invalidDep));
      }
    }
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
