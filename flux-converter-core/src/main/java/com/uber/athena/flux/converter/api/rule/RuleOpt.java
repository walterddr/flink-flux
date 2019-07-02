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

package com.uber.athena.flux.converter.api.rule;

import com.uber.athena.flux.converter.api.node.Node;
import com.uber.athena.flux.model.StreamDef;

import java.util.List;

public interface RuleOpt {

  /**
   * Returns the current vertex node that matched by this rule.
   *
   * @return the node itself
   */
  <T extends Node> T getNode();

  /**
   * Returns rule desired to be invoked.
   *
   * @return the rule itself
   */
  Rule getRule();

  /**
   * Returns a upstream node associated with this node via a {@link StreamDef}.
   *
   * <p>Upstream nodes should be formed via converter context
   * to associate correct format of the node for this particular
   * rule's requirement.
   *
   * @param vertexId the node vertex ID.
   * @return the upstream node of the vertexId matched in this rule's context.
   */
  <R extends Node> R getUpstreamNode(String vertexId);

  /**
   * Retrieves a list of upstream definitions connecting with this node.
   *
   * @return the upstream list.
   */
  List<StreamDef> getUpstreams();

  /**
   * Retrieves a list of downstream definitions connecting with this node.
   *
   * @return the downstream list.
   */
  List<StreamDef> getDownstreams();
}
