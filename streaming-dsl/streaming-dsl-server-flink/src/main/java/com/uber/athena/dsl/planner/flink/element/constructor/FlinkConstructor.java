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

package com.uber.athena.dsl.planner.flink.element.constructor;

import com.uber.athena.dsl.planner.element.constructor.ComponentResolutionUtils;
import com.uber.athena.dsl.planner.element.constructor.ConstructorImpl;
import com.uber.athena.dsl.planner.element.constructor.ReflectiveConstructUtils;
import com.uber.athena.dsl.planner.flink.element.FlinkElement;
import com.uber.athena.dsl.planner.flink.type.FlinkType;
import com.uber.athena.dsl.planner.flink.type.FlinkTypeFactory;
import com.uber.athena.dsl.planner.model.VertexNode;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.type.TypeFactory;
import com.uber.athena.dsl.planner.utils.ConstructionException;

import java.util.List;

/**
 * Flink implementation of the constructor class.
 */
public class FlinkConstructor extends ConstructorImpl {

  // TODO: @walterddr override this to support 3rd party lib loading.
  @Override
  public FlinkElement construct(
      VertexNode vertex,
      Topology topology,
      TypeFactory typeFactory) throws ConstructionException {
    try {
      // construct Flink type factory.
      FlinkTypeFactory flinkTypeFactory = (FlinkTypeFactory) typeFactory;
      // Resolve references
      List<Object> resolvedConstructorArgs = ComponentResolutionUtils.resolveReferences(
          vertex.getVertexDef().getConstructorArgs(), topology, constructMap);
      if (resolvedConstructorArgs != null) {
        vertex.getVertexDef().setConstructorArgs(resolvedConstructorArgs);
      }
      // Construct the element from vertex using reflection.
      Object obj = ReflectiveConstructUtils.buildObject(
          vertex.getVertexDef(), topology, constructMap);
      Class<?> clazz = Class.forName(vertex.getVertexDef().getClassName());

      // Construct the Flink TypeInformation.
      FlinkType type = flinkTypeFactory.getType(vertex.getVertexDef().getTypeSpec());

      return new FlinkElement(obj, clazz, type);
    } catch (Exception e) {
      throw new ConstructionException("Cannot construct element!", e);
    }
  }
}
