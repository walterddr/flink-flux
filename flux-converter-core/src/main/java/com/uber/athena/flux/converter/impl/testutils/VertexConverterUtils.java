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

package com.uber.athena.flux.converter.impl.testutils;

import com.uber.athena.flux.converter.api.converter.ConverterContext;
import com.uber.athena.flux.converter.api.traverser.TraverserContext;
import com.uber.athena.flux.model.ComponentDef;
import com.uber.athena.flux.model.ComponentRefDef;
import com.uber.athena.flux.model.PropertyDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class VertexConverterUtils {
  private static final Logger LOG = LoggerFactory.getLogger(VertexConverterUtils.class);

  private VertexConverterUtils() {
    // do not instantiate.
  }

  // ------------------------------------------------------------------------
  // Reference Resolve utilities
  // ------------------------------------------------------------------------

  /**
   * Find referenced objects based on component name.
   *
   * @param args             arguments
   * @param traverserContext the context used to search for topology linked objects.
   * @param converterContext the used to search for reference objects.
   * @return java.lang.Method
   */
  public static List<Object> resolveReferences(
      List<Object> args,
      TraverserContext traverserContext,
      ConverterContext converterContext) {
    LOG.debug("Checking arguments for references.");
    List<Object> cArgs = new ArrayList<Object>();
    // resolve references
    Map<String, ComponentDef> componentMap =
        traverserContext.getTopologyDef().getComponents();
    if (args != null) {
      for (Object arg : args) {
        if (arg instanceof ComponentRefDef) {
          ComponentDef component = componentMap.get(((ComponentRefDef) arg).getId());
          if (component == null) {
            throw new IllegalArgumentException("TraverserContext does not contain component"
                + " reference for: " + arg);
          }
          cArgs.add(component);
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
   * @param traverserContext the context used to search for topology linked objects.
   * @param converterContext the used to search for reference objects.
   * @return mapped arguments with its identifiers.
   */
  public static Map<String, Object> resolveProperties(
      List<PropertyDef> props,
      TraverserContext traverserContext,
      ConverterContext converterContext) {
    Map<String, Object> resolvedPropertyToObjectMap = new HashMap<>();
    Map<String, ComponentDef> componentMap =
        traverserContext.getTopologyDef().getComponents();

    for (PropertyDef prop : props) {
      Object value = prop.getReference() != null
          ? componentMap.get(prop.getReference()) : prop.getValue();
      resolvedPropertyToObjectMap.put(prop.getName(), value);
    }
    return resolvedPropertyToObjectMap;
  }
}
