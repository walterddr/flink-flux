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

/**
 * Node that represents a vertex with its constructed object.
 *
 * <p>This {code ElementNode} is transformed from the {@code DslNode}. This
 * constructed object should be platform-specific: each platform should
 * implement sets of element nodes where different conversion rules applies.
 *
 * <p>The constructed object also requires enrichment around the them, such
 * as platform-specific type system conversion. In general these rules are
 * usually specific to the element node type: they cannot be applied generally.
 *
 * <p><ul>
 * <li>object-instantiation
 * <li>type-checking
 * <li>...
 * </ul></p>
 */
public interface ElementNode {

  /**
   * Returns the type of element this node is capable of constructing.
   *
   * @return Clazz of the element object.
   */
  Class<?> getElementClass();

  /**
   * Return the constructed element.
   *
   * <p>return element type should match element class acquired from the
   * {@code ElementNode.getElementClass} method.
   *
   * @param <R> result matching return type
   * @return the constructed element, or null if not constructed.
   */
  <R> R getElement();
}
