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

package com.uber.athena.dsl.planner.type;

import com.uber.athena.dsl.planner.model.ArrayTypeDef;
import com.uber.athena.dsl.planner.model.MapTypeDef;
import com.uber.athena.dsl.planner.model.RowTypeDef;
import com.uber.athena.dsl.planner.model.TypeDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Type spec conversion utils.
 */
@SuppressWarnings("unchecked")
public final class TypeSpecUtils {

  private static final String ARRAY_ELEMENT_TYPE_KEY = "__ELEMENT_TYPE";
  private static final String MAP_KEY_TYPE_KEY = "__KEY_TYPE";
  private static final String MAP_VALUE_TYPE_KEY = "__VALUE_TYPE";

  private TypeSpecUtils() {
  }

  public static TypeDef recursiveResolveType(Object typeObj) {
    if (typeObj instanceof Map) {
      TypeDef typeDef = null;
      Map<String, Object> compositeTypeObj = (Map<String, Object>) typeObj;
      // Resolve Array Type
      typeDef = resolveArrayType(compositeTypeObj);
      if (typeDef != null) {
        return typeDef;
      }
      // Resolve Map Type
      typeDef = resolveMapType(compositeTypeObj);
      if (typeDef != null) {
        return typeDef;
      }
      // Resolve Row Type
      typeDef = resolveRowType(compositeTypeObj);
      if (typeDef != null) {
        return typeDef;
      }
    } else if (typeObj instanceof TypeDef || typeObj instanceof String) {
      TypeDef basicTypeObj;
      if (typeObj instanceof String) {
        basicTypeObj = new TypeDef().type(
            TypeDef.TypeEnum.fromValue((String) typeObj));
      } else {
        basicTypeObj = (TypeDef) typeObj;
      }
      return basicTypeObj;
    }
    throw new IllegalArgumentException("Cannot construct for type: " + typeObj);
  }

  private static TypeDef resolveArrayType(Map<String, Object> typeObj) {
    if (typeObj.size() == 1) {
      Map.Entry<String, Object> e = typeObj.entrySet().iterator().next();
      if (e.getKey().equals(ARRAY_ELEMENT_TYPE_KEY)) {
        return new ArrayTypeDef()
            .elementType(recursiveResolveType(e.getValue()))
            .type(TypeDef.TypeEnum.ARRAY_TYPE);
      }
    }
    return null;
  }

  private static TypeDef resolveMapType(Map<String, Object> typeObj) {
    if (typeObj.size() == 2) {
      TypeDef keyType = null;
      TypeDef valueType = null;
      for (Map.Entry<String, Object> e : typeObj.entrySet()) {
        if (e.getKey().equals(MAP_KEY_TYPE_KEY)) {
          keyType = recursiveResolveType(e.getValue());
        }
        if (e.getKey().equals(MAP_VALUE_TYPE_KEY)) {
          valueType = recursiveResolveType(e.getValue());
        }
      }
      if (keyType != null && valueType != null) {
        return new MapTypeDef()
            .keyType(keyType)
            .valueType(valueType)
            .type(TypeDef.TypeEnum.MAP_TYPE);
      }
    }
    return null;
  }

  private static TypeDef resolveRowType(Map<String, Object> typeObj) {
    List<TypeDef> types = new ArrayList<>();
    List<String> names = new ArrayList<>();
    for (Map.Entry<String, Object> e : typeObj.entrySet()) {
      names.add(e.getKey());
      types.add(recursiveResolveType(e.getValue()));
    }
    return new RowTypeDef()
        .fieldNames(names)
        .fieldTypes(types)
        .type(TypeDef.TypeEnum.ROW_TYPE);
  }
}
