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

package com.uber.athena.dsl.planner.element.convertlet;

/**
 * Base class of all convertlets. converts between two of the Node types.
 *
 * <p>Conversion does not necessary has to change the type of node. It can
 * also convert between the same type of node. such as
 *
 * <p><ul>
 * <li>{@link ModelNode}
 * <li>{@link ElementNode}
 * <li>{@link RelationNode}
 * <li>...
 * </ul></p>
 */
public interface Convertlet {
}
