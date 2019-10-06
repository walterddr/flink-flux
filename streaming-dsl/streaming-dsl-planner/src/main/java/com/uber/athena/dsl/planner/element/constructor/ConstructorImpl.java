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

import com.uber.athena.dsl.planner.element.Element;
import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.model.VertexNode;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.type.Type;
import com.uber.athena.dsl.planner.type.TypeFactory;
import com.uber.athena.dsl.planner.utils.ConstructionException;

import java.util.List;

/**
 * Base implementation of the {@link Constructor}.
 *
 * <p>This base impl does not have any extended capabilities such as, dynamic
 * service loader, supporting of 3rd party library that's not loaded inside
 * the classpath during JVM start up, etc.
 */
public class ConstructorImpl implements Constructor {

  public ConstructorImpl() {
  }

  @Override
  public ElementNode construct(
      VertexNode vertex,
      Topology topology,
      TypeFactory typeFactory) throws ConstructionException {
    // TODO @walterddr actually using the loader factory
    // instead of directly using the reflective construct util
    try {
      // Resolve references
      List<Object> resolvedConstructorArgs = ComponentResolutionUtils.resolveReferences(
          vertex.getVertexDef().getConstructorArgs(), topology);
      if (resolvedConstructorArgs != null) {
        vertex.getVertexDef().setConstructorArgs(resolvedConstructorArgs);
      }
      // Construct the element from vertex using reflection.
      Object obj = ReflectiveConstructUtils.buildObject(vertex.getVertexDef(), topology);
      Class<?> clazz = Class.forName(vertex.getVertexDef().getClassName());

      // Construct the produce type of the vertex.
      Type type = typeFactory.getType(vertex.getVertexDef().getTypeSpec());

      return new Element(obj, clazz, type);
    } catch (Exception e) {
      throw new ConstructionException("Cannot construct element!", e);
    }
  }
}
