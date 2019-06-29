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

package com.uber.athena.flux.converter.api.node.dsl;

import com.uber.athena.flux.converter.api.node.Node;

/**
 * Node that represents a vertex its attributes in the DSL system.
 *
 * <p>This {code DslNode} can be considered as a vertex enriched based on
 * the topology DAG defined in the DSL model. Mostly resolve components that
 * are defined but not able to resolved during parsing time.
 *
 * <p>Type of enriched attributes should be platform invariant - they should
 * only relate to the DSL model. For example:
 *
 * <p><ul>
 * <li>semantically-verification
 * <li>schema-resolution
 * <li>type-checking
 * <li>...
 * </ul></p>
 */
public interface DslNode extends Node {
}
