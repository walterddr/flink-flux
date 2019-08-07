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

package com.uber.athena.dsl.utils;

import com.uber.athena.dsl.model.ComponentDef;
import com.uber.athena.dsl.model.ComponentRefDef;
import com.uber.athena.dsl.model.ConfigMethodDef;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reference util for inter component referencing.
 */
public final class ReferenceUtils {

  private ReferenceUtils() {
    // do not instantiate.
  }

  public static void setConfigMethodArgs(ConfigMethodDef configMethod, List<Object> args) {

    List<Object> newVal = new ArrayList<Object>();
    for (Object obj : args) {
      if (obj instanceof LinkedHashMap) {
        Map map = (Map) obj;
        if (map.containsKey("ref") && map.size() == 1) {
          ComponentRefDef componentRefDef = new ComponentRefDef();
          componentRefDef.setId((String) map.get("ref"));
          newVal.add(componentRefDef);
          configMethod.setHasReferenceInArgs(true);
        }
      } else {
        newVal.add(obj);
      }
    }
    configMethod.setConfigArgs(newVal);
  }

  public void setConstructorArgs(ComponentDef componentDef, List<Object> constructorArgs) {

    List<Object> newVal = new ArrayList<Object>();
    for (Object obj : constructorArgs) {
      if (obj instanceof LinkedHashMap) {
        Map map = (Map) obj;
        if (map.containsKey("ref") && map.size() == 1) {
          ComponentRefDef componentRefDef = new ComponentRefDef();
          componentRefDef.setId((String) map.get("ref"));
          newVal.add(componentRefDef);
          componentDef.setHasReferenceInArgs(true);
        } else {
          newVal.add(obj);
        }
      } else {
        newVal.add(obj);
      }
    }
    componentDef.setConstructorArgs(newVal);
  }
}
