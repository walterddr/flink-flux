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

package com.uber.athena.dsl.planner.topology;

import com.uber.athena.dsl.planner.model.ComponentDef;
import com.uber.athena.dsl.planner.model.ComponentRefDef;
import com.uber.athena.dsl.planner.model.ObjectDef;
import com.uber.athena.dsl.planner.model.StreamDef;
import com.uber.athena.dsl.planner.model.StreamSpecDef;
import com.uber.athena.dsl.planner.model.TopologyDef;
import com.uber.athena.dsl.planner.model.Vertex;
import com.uber.athena.dsl.planner.model.VertexDef;
import com.uber.athena.dsl.planner.model.VertexUtils;
import com.uber.athena.dsl.planner.utils.TopologyBuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@link TopologyBuilder} implementation for {@link DslTopology}.
 */
public class DslTopologyBuilder implements TopologyBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(DslTopologyBuilder.class);

  @Override
  public Topology createTopology(
      TopologyDef topologyDef,
      Map<String, Object> config) throws TopologyBuilderException {
    TopologyBuilderContext context = new TopologyBuilderContext(topologyDef);
    try {
      return context.build();
    } catch (Exception e) {
      throw new TopologyBuilderException("Unable to construct topology!", e);
    }
  }

  static class TopologyBuilderContext {
    private final TopologyDef topologyDef;
    private final Map<String, VertexDef> vertexMap;
    private final Map<String, Map<String, StreamDef>> upstreamMap;

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
        addItemToMap(this.upstreamMap, to, from, stream);

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
      if (this.topologyDef.getDependencies() != null) {
        topology.getDependencies().addAll(this.topologyDef.getDependencies());
      }
      if (this.topologyDef.getPropertyMap() != null) {
        topology.getPropertyMap().putAll(this.topologyDef.getPropertyMap());
      }
      if (this.topologyDef.getComponents() != null) {
        topology.getComponents().putAll(
            this.topologyDef.getComponents().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                  ComponentDef componentDef = e.getValue();
                  processObjectDef(componentDef);
                  return componentDef;
                })
            ));
      }

      // Add all vertices.
      this.vertexMap.forEach((k, v) -> {
        // dereference all vertex
        processObjectDef(v);
        // check in case of unconnected vertex (no upstream and downstream)
        if (!upstreamVertexMap.containsKey(k) && !downstreamVertexMap.containsKey(k)) {
          LOG.warn("unconnected vertex {}", k);
        }
        Vertex vertex = constructVertex(v);
        if (!upstreamVertexMap.containsKey(k) || upstreamVertexMap.get(k).size() == 0) {
          topology.getSources().put(k, vertex);
        } else if (!downstreamVertexMap.containsKey(k) || downstreamVertexMap.get(k).size() == 0) {
          topology.getSinks().put(k, vertex);
        } else {
          topology.getOperators().put(k, vertex);
        }
        topology.putVertex(k, vertex);
      });

      return topology;
    }

    private Vertex constructVertex(VertexDef def) {
      String id = def.getId();
      Vertex dslVertex = VertexUtils.construct(id, def);
      VertexUtils.addUpstreamVertices(dslVertex, this.upstreamVertexMap.get(id));
      VertexUtils.addDownstreamVertices(dslVertex, this.downstreamVertexMap.get(id));
      VertexUtils.addStreams(dslVertex, this.upstreamMap.get(id));
      return dslVertex;
    }

    private static StreamSpecDef getDefaultStreamSpec() {
      StreamSpecDef defaultSpec = new StreamSpecDef();
      defaultSpec.setStreamType(StreamSpecDef.StreamTypeEnum.DATA_STREAM);
      return defaultSpec;
    }

    private static <T> void addItemToList(Map<String, ArrayList<T>> m, String key, T v) {
      ArrayList<T> list = m.get(key);
      if (list == null) {
        list = new ArrayList<>();
        list.add(v);
        m.put(key, list);
      } else {
        list.add(v);
      }
    }

    private static <T> void addItemToMap(Map<String, Map<String, T>> m, String key, String k, T v) {
      Map<String, T> map = m.get(key);
      if (map == null) {
        map = new HashMap<>();
        map.put(k, v);
        m.put(key, map);
      } else {
        map.put(k, v);
      }
    }

    /**
     * In-place pre-processing of the object definition.
     *
     * <p>This in-place utility only de-reference objects in arguments as of
     * now.
     *
     * @param objectDef the object definition.
     */
    @SuppressWarnings("unchecked")
    private static void processObjectDef(ObjectDef objectDef) {
      // de-referencing the object.
      List<Object> args = objectDef.getConstructorArgs();
      if (args != null) {
        List<Object> newArgs = new ArrayList<Object>();
        for (Object obj : args) {
          if (obj instanceof LinkedHashMap) {
            Map<String, Object> map = (Map<String, Object>) obj;
            if (map.containsKey("ref") && map.size() == 1) {
              newArgs.add(new ComponentRefDef().id((String) map.get("ref")));
              objectDef.setHasReferenceInArgs(true);
            } else {
              newArgs.add(obj);
            }
          } else {
            newArgs.add(obj);
          }
        }
        objectDef.setConstructorArgs(newArgs);
      }
    }
  }
}
