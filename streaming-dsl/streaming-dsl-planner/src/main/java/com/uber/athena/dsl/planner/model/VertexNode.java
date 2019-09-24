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

package com.uber.athena.dsl.planner.model;

import java.util.List;
import java.util.Map;

/**
 * Model node represents a vertex in a topology.
 *
 * <p>Model node refer to the modeling vertex which is runtime invariant.
 * regardless of what underlying runtime environment is. the model node
 * should not change.
 */
public interface VertexNode {

  String getVertexId();

  VertexDef getVertexDef();

  List<String> getUpstreamVertexIds();

  List<String> getDownstreamVertexIds();

  Map<String, StreamDef> getUpstreams();

  StreamDef getUpstream(String upstreamVertexId);
}
