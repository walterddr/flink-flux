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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility to construct a {@link Vertex} based on a DSL topology.
 */
public final class VertexUtils {

  private VertexUtils() {
  }

  public static Vertex construct(String id, VertexDef def) {
    return new Vertex(id, def, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
  }

  public static void addUpstreamVertices(Vertex vertex, List<String> upstream) {
    if (upstream != null) {
      vertex.getUpstreamVertexIds().addAll(upstream);
    }
  }

  public static void addDownstreamVertices(Vertex vertex, List<String> downstream) {
    if (downstream != null) {
      vertex.getDownstreamVertexIds().addAll(downstream);
    }
  }

  public static void addStreams(Vertex vertex, Map<String, StreamDef> streams) {
    if (streams != null) {
      vertex.getUpstreams().putAll(streams);
    }
  }

  public static void addUpstreamVertexId(Vertex vertex, String upstreamVertexId) {
    vertex.getUpstreamVertexIds().add(upstreamVertexId);
  }

  public static void addDownstreamVertexId(Vertex vertex, String upstreamVertexId) {
    vertex.getDownstreamVertexIds().add(upstreamVertexId);
  }

  public static void addUpstream(Vertex vertex, String upstreamKey, StreamDef streamDef) {
    vertex.getUpstreams().put(upstreamKey, streamDef);
  }
}
