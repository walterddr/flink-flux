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
import java.util.List;

/**
 * Utility to construct a {@link ModelVertex}.
 */
public final class ModelVertexUtils {

  private ModelVertexUtils() {
  }

  public static ModelVertex construct(String id, VertexDef def) {
    return new ModelVertex(id, def, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  public static void addUpstreamVertices(ModelVertex vertex, List<String> upstream) {
    if (upstream != null) {
      vertex.getUpstreamVertexIds().addAll(upstream);
    }
  }

  public static void addDownstreamVertices(ModelVertex vertex, List<String> downstream) {
    if (downstream != null) {
      vertex.getDownstreamVertexIds().addAll(downstream);
    }
  }

  public static void addStreams(ModelVertex vertex, List<StreamDef> streams) {
    if (streams != null) {
      vertex.getUpstreams().addAll(streams);
    }
  }

  public static void addUpstreamVertexId(ModelVertex vertex, String upstreamVertexId) {
    vertex.getUpstreamVertexIds().add(upstreamVertexId);
  }

  public static void addDownstreamVertexId(ModelVertex vertex, String upstreamVertexId) {
    vertex.getDownstreamVertexIds().add(upstreamVertexId);
  }

  public static void addUpstream(ModelVertex vertex, StreamDef streamDef) {
    vertex.getUpstreams().add(streamDef);
  }
}
