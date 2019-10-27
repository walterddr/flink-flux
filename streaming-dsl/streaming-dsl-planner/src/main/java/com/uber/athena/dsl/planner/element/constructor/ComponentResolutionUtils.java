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

import com.uber.athena.dsl.planner.model.ComponentDef;
import com.uber.athena.dsl.planner.model.ComponentRefDef;
import com.uber.athena.dsl.planner.model.PropertyDef;
import com.uber.athena.dsl.planner.model.TypeSpecDef;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.type.TypeSpecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility to resolve references, properties and environment variables.
 */
public final class ComponentResolutionUtils {
  private static final Logger LOG = LoggerFactory.getLogger(ComponentResolutionUtils.class);

  private ComponentResolutionUtils() {
    // do not instantiate.
  }

  // ------------------------------------------------------------------------
  // Reference Resolve utilities
  // ------------------------------------------------------------------------

  /**
   * Find referenced objects based on component name.
   *
   * <p>This utility replaces any reference component identifier with actual
   * component objects, if the object is originally a component it will reused.
   * Replacement preserves cardinality and order.
   *
   * @param args constructor arguments.
   * @param topology the topology defined via DSL model.
   * @return a list of resolved reference objects
   */
  public static List<Object> resolveReferences(
      List<Object> args,
      Topology topology,
      Map<String, Object> constructMap) {
    LOG.debug("Checking arguments for references.");
    List<Object> cArgs = new ArrayList<Object>();
    // resolve references
    Map<String, ComponentDef> componentMap = topology.getComponents();
    if (args != null) {
      for (Object arg : args) {
        if (arg instanceof ComponentRefDef) {
          String componentId = ((ComponentRefDef) arg).getId();
          ComponentDef component = componentMap.get(componentId);
          if (component == null) {
            throw new IllegalArgumentException("TraverserContext does not contain component"
                + " reference for: " + arg);
          }
          if (constructMap.get(componentId) != null) {
            cArgs.add(constructMap.get(componentId));
          } else {
            try {
              Object obj = ReflectiveConstructUtils.buildObject(component, topology, constructMap);
              cArgs.add(obj);
              constructMap.put(componentId, obj);
            } catch (ReflectiveOperationException e) {
              throw new IllegalArgumentException("Unable to construct component!", e);
            }
          }
        } else {
          cArgs.add(arg);
        }
      }
      return cArgs;
    } else {
      return null;
    }
  }

  /**
   * Find referenced properties for replacement.
   *
   * @param props            list of property definitions.
   * @return mapped arguments with its identifiers.
   */
  public static Map<String, Object> resolveProperties(
      List<PropertyDef> props,
      Topology topology) {
    Map<String, Object> resolvedPropertyToObjectMap = new HashMap<>();
    Map<String, ComponentDef> componentMap = topology.getComponents();

    for (PropertyDef prop : props) {
      Object value = prop.getReference() != null
          ? componentMap.get(prop.getReference()) : prop.getValue();
      resolvedPropertyToObjectMap.put(prop.getName(), value);
    }
    return resolvedPropertyToObjectMap;
  }

  public static TypeSpecDef resolveTypeSpecDef(TypeSpecDef typeSpecDef) {
    typeSpecDef.setTypeDef(TypeSpecUtils.recursiveResolveType(typeSpecDef.getTypeDef()));
    return typeSpecDef;
  }
}
