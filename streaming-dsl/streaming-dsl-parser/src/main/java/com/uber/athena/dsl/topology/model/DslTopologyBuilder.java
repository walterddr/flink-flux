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

package com.uber.athena.dsl.topology.model;

import com.uber.athena.dsl.model.StreamDef;
import com.uber.athena.dsl.model.StreamSpecDef;
import com.uber.athena.dsl.model.TopologyDef;
import com.uber.athena.dsl.model.VertexDef;
import com.uber.athena.dsl.topology.api.Topology;
import com.uber.athena.dsl.topology.api.TopologyBuilder;
import com.uber.athena.dsl.topology.api.Vertex;
import com.uber.athena.dsl.topology.exceptions.ConstructionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@link TopologyBuilder} implementation for {@link DslTopology}.
 */
public class DslTopologyBuilder implements TopologyBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(DslTopologyBuilder.class);

  @Override
  public Topology createTopology(
      TopologyDef topologyDef,
      Map<String, Object> config) throws ConstructionException {
    TopologyBuilderContext context = new TopologyBuilderContext(topologyDef);
    try {
      return context.build();
    } catch (Exception e) {
      throw new ConstructionException("Unable to construct topology!", e);
    }
  }

  static class TopologyBuilderContext {
    private final TopologyDef topologyDef;
    private final Map<String, VertexDef> vertexMap;
    private final Map<String, ArrayList<StreamDef>> upstreamMap;

    private final Map<String, ArrayList<String>> upstreamVertexMap;
    private final Map<String, ArrayList<String>> downstreamVertexMap;

    TopologyBuilderContext(TopologyDef topologyDef) {
      this.topologyDef = topologyDef;
      this.vertexMap = new HashMap<>();
      this.upstreamVertexMap = new HashMap<>();
      this.downstreamVertexMap = new HashMap<>();
      this.upstreamMap = new HashMap<>();
    }

    Topology build() {
      return this
          .parseVertex(this.topologyDef.getVertices().values())
          .parseStream(this.topologyDef.getStreams())
          .constructDag();
    }

    TopologyBuilderContext parseVertex(Collection<VertexDef> v) {
      v.forEach(vertex -> {
        String id = vertex.getId();
        vertexMap.put(id, vertex);
      });
      return this;
    }

    TopologyBuilderContext parseStream(Collection<StreamDef> s) {
      s.forEach(stream -> {
        String from = stream.getFromVertex();
        String to = stream.getToVertex();

        // add stream to stream map.
        if (stream.getStreamSpec() == null) {
          stream.setStreamSpec(getDefaultStreamSpec());
        }
        addItemToList(this.upstreamMap, to, stream);

        // assert that the vertex IDs are valid.
        assert vertexMap.containsKey(from);
        assert vertexMap.containsKey(to);

        // add vertex to up/down stream mapping list
        addItemToList(this.upstreamVertexMap, to, from);
        addItemToList(this.downstreamVertexMap, from, to);
      });
      return this;
    }

    Topology constructDag() {
      final DslTopology topology = new DslTopology(this.topologyDef.getName());

      // Add all streams.
      assert this.topologyDef.getStreams() != null;
      topology.getStreams().addAll(this.topologyDef.getStreams());

      // Add all configs/properties/components.
      if (this.topologyDef.getConfig() != null) {
        topology.getConfig().putAll(this.topologyDef.getConfig());
      }
      if (this.topologyDef.getPropertyMap() != null) {
        topology.getPropertyMap().putAll(this.topologyDef.getPropertyMap());
      }
      if (this.topologyDef.getComponents() != null) {
        topology.getComponents().putAll(this.topologyDef.getComponents());
      }

      // Add all vertices.
      this.vertexMap.forEach((k, v) -> {
        // check in case of unconnected vertex (no upstream and downstream)
        if (!upstreamVertexMap.containsKey(k) && !downstreamVertexMap.containsKey(k)) {
          LOG.warn("unconnected vertex {}", k);
        }
        if (!upstreamVertexMap.containsKey(k) || upstreamVertexMap.get(k).size() == 0) {
          topology.getSources().put(k, constructVertex(v));
        } else if (!downstreamVertexMap.containsKey(k) || downstreamVertexMap.get(k).size() == 0) {
          topology.getSinks().put(k, constructVertex(v));
        } else {
          topology.getOperators().put(k, constructVertex(v));
        }
      });

      return topology;
    }

    private Vertex constructVertex(VertexDef def) {
      String id = def.getId();
      DslVertex dslVertex = new DslVertex(id, def);
      dslVertex.addUpstreamVertices(this.upstreamVertexMap.get(id));
      dslVertex.addDownstreamVertices(this.downstreamVertexMap.get(id));
      dslVertex.addStreams(this.upstreamMap.get(id));
      return dslVertex;
    }

    private static StreamSpecDef getDefaultStreamSpec() {
      StreamSpecDef defaultSpec = new StreamSpecDef();
      defaultSpec.setStreamType(StreamSpecDef.StreamTypeEnum.DATA_STREAM);
      return defaultSpec;
    }

    private static <T> void addItemToList(Map<String, ArrayList<T>> m, String k, T v) {
      ArrayList<T> list = m.get(k);
      if (list == null) {
        list = new ArrayList<>();
      }
      list.add(v);
      m.put(k, list);
    }
  }
}
