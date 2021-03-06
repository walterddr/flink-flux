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

package com.uber.athena.dsl.planner.element;

import com.uber.athena.dsl.planner.type.Type;

/**
 * Base implementation of an {@link ElementNode}.
 */
@SuppressWarnings("unchecked")
public class Element implements ElementNode {

  private Object obj;
  private Type type;
  private Class<?> clazz;

  public Element(Object obj, Class<?> clazz) {
    this(obj, clazz, null);
  }

  public Element(Object obj, Class<?> clazz, Type type) {
    this.obj = obj;
    this.clazz = clazz;
    this.type = type;
  }

  @Override
  public Class<?> getElementClass() {
    return this.clazz;
  }

  @Override
  public <R> R getElement() {
    return (R) this.obj;
  }

  @Override
  public <T extends Type> T getProduceType() {
    return (T) type;
  }
}
