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

package com.uber.athena.flux.utils;

import com.uber.athena.flux.model.ComponentDef;
import com.uber.athena.flux.model.OperatorDef;
import com.uber.athena.flux.model.SinkDef;
import com.uber.athena.flux.model.SourceDef;
import com.uber.athena.flux.model.StreamDef;
import com.uber.athena.flux.model.TopologyDef;
import com.uber.athena.flux.model.VertexDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * Bean represenation of a topology.
 *
 * <p>It consists of the following:
 * 1. The topology name
 * 2. A `java.util.Map` representing the Flink {@code Configuration} for the topology
 * 3. A list of source definitions
 * 4. A list of operator definitions
 * 5. A list of sink definitions
 * 6. A list of stream definitions that define the flow between components.
 */
public final class TopologyUtils {
  private static final Logger LOG = LoggerFactory.getLogger(com.uber.athena.flux.model.TopologyDef.class);

  private TopologyUtils() {
    // do not instantiate.
  }

  // utility methods
  public static int getVertexParallism(TopologyDef def, String id) {
    return ((VertexDef) def.getComponents().get(id)).getParallelism();
  }

  public static OperatorDef getOperatorDef(TopologyDef def, String id) {
    return def.getOperators().get(id);
  }

  public static SourceDef getSourceDef(TopologyDef def, String id) {
    return def.getSources().get(id);
  }

  public static SinkDef getSinkDef(TopologyDef def, String id) {
    return def.getSinks().get(id);
  }

  public ComponentDef getComponent(TopologyDef def, String id) {
    return def.getComponents().get(id);
  }

  public static Collection<SourceDef> getSourceList(TopologyDef topologyDef) {
    return topologyDef.getSources() == null
        ? Collections.emptyList() : topologyDef.getSources().values();
  }

  public static Collection<SinkDef> getSinkList(TopologyDef topologyDef) {
    return topologyDef.getSinks() == null
        ? Collections.emptyList() : topologyDef.getSinks().values();
  }

  public static Collection<OperatorDef> getOperatorList(TopologyDef topologyDef) {
    return topologyDef.getOperators() == null
        ? Collections.emptyList() : topologyDef.getOperators().values();
  }

  // used by includes implementation
  public static void addAllOperators(TopologyDef def, Collection<OperatorDef> operators, boolean override) {
    for (OperatorDef operator : operators) {
      String id = operator.getId();
      if (def.getOperators().get(id) == null || override) {
        def.getOperators().put(operator.getId(), operator);
      } else {
        LOG.warn("Ignoring attempt to create operator '{}' with override == false.", id);
      }
    }
  }

  public static void addAllSources(TopologyDef def, Collection<SourceDef> sources, boolean override) {
    for (SourceDef source : sources) {
      String id = source.getId();
      if (def.getSources().get(id) == null || override) {
        def.getSources().put(source.getId(), source);
      } else {
        LOG.warn("Ignoring attempt to create source '{}' with override == false.", id);
      }
    }
  }

  public static void addAllSinks(TopologyDef def, Collection<SinkDef> sinks, boolean override) {
    for (SinkDef sink : sinks) {
      String id = sink.getId();
      if (def.getSinks().get(id) == null || override) {
        def.getSinks().put(sink.getId(), sink);
      } else {
        LOG.warn("Ignoring attempt to create sink '{}' with override == false.", id);
      }
    }
  }

  public static void addAllComponents(TopologyDef def, Collection<ComponentDef> components, boolean override) {
    for (ComponentDef component : components) {
      String id = component.getId();
      if (def.getComponents().get(id) == null || override) {
        def.getComponents().put(component.getId(), component);
      } else {
        LOG.warn("Ignoring attempt to create component '{}' with override == false.", id);
      }
    }
  }

  public static void addAllStreams(TopologyDef def, Collection<StreamDef> streams, boolean override) {
    def.getStreams().addAll(streams);
  }

  /**
   * validate that the definition actually represents a valid Flux topology.
   * @return true if the topology def is valid.
   */
  public static boolean validate(TopologyDef def) {
    boolean hasSources = def.getSources() != null && def.getSources().size() > 0;
    boolean hasSinks = def.getSinks() != null && def.getSinks().size() > 0;
    boolean hasStreams = def.getStreams() != null && def.getStreams().size() > 0;
    if (hasSources && hasSinks && hasStreams) {
      return true;
    }
    return true;
  }
}
