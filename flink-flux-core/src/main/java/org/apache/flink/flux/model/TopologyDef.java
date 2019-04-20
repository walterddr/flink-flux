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

package org.apache.flink.flux.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean represenation of a topology.
 *
 * <p>It consists of the following:
 * 1. The topology name
 * 2. A `java.util.Map` representing the `org.apache.storm.config` for the topology
 * 3. A list of spout definitions
 * 4. A list of bolt definitions
 * 5. A list of stream definitions that define the flow between spouts and bolts.
 */
public class TopologyDef {
  private static final Logger LOG = LoggerFactory.getLogger(TopologyDef.class);

  private String name;
  private Map<String, ComponentDef> componentMap = new LinkedHashMap<String, ComponentDef>();
  private List<IncludeDef> includes;
  private Map<String, Object> config = new HashMap<String, Object>();

  // the following are required if we're defining a core storm topology DAG in YAML, etc.
  private Map<String, OperatorDef> operatorMap = new LinkedHashMap<String, OperatorDef>();
  private Map<String, SourceDef> sourceMap = new LinkedHashMap<String, SourceDef>();
  private Map<String, SinkDef> sinkMap = new LinkedHashMap<String, SinkDef>();
  private List<StreamDef> streams = new ArrayList<StreamDef>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setName(String name, boolean override) {
    if (this.name == null || override) {
      this.name = name;
    } else {
      LOG.warn("Ignoring attempt to set property 'name' with override == false.");
    }
  }

  public List<SourceDef> getSources() {
    ArrayList<SourceDef> retval = new ArrayList<SourceDef>();
    retval.addAll(this.sourceMap.values());
    return retval;
  }

  public void setSources(List<SourceDef> sources) {
    this.sourceMap = new LinkedHashMap<String, SourceDef>();
    for (SourceDef spout : sources) {
      this.sourceMap.put(spout.getId(), spout);
    }
  }

  public List<OperatorDef> getOperators() {
    ArrayList<OperatorDef> retval = new ArrayList<OperatorDef>();
    retval.addAll(this.operatorMap.values());
    return retval;
  }

  public void setOperators(List<OperatorDef> operators) {
    this.operatorMap = new LinkedHashMap<String, OperatorDef>();
    for (OperatorDef bolt : operators) {
      this.operatorMap.put(bolt.getId(), bolt);
    }
  }

  public List<SinkDef> getSinks() {
    ArrayList<SinkDef> retval = new ArrayList<SinkDef>();
    retval.addAll(this.sinkMap.values());
    return retval;
  }

  public void setSinks(List<SinkDef> sinks) {
    this.sinkMap = new LinkedHashMap<String, SinkDef>();
    for (SinkDef bolt : sinks) {
      this.sinkMap.put(bolt.getId(), bolt);
    }
  }

  public List<StreamDef> getStreams() {
    return streams;
  }

  public void setStreams(List<StreamDef> streams) {
    this.streams = streams;
  }

  public Map<String, Object> getConfig() {
    return config;
  }

  public void setConfig(Map<String, Object> config) {
    this.config = config;
  }

  public List<ComponentDef> getComponents() {
    ArrayList<ComponentDef> retval = new ArrayList<ComponentDef>();
    retval.addAll(this.componentMap.values());
    return retval;
  }

  public void setComponents(List<ComponentDef> components) {
    this.componentMap = new LinkedHashMap<String, ComponentDef>();
    for (ComponentDef component : components) {
      this.componentMap.put(component.getId(), component);
    }
  }

  public List<IncludeDef> getIncludes() {
    return includes;
  }

  public void setIncludes(List<IncludeDef> includes) {
    this.includes = includes;
  }

  // utility methods
  public int parallelismForBolt(String boltId) {
    return this.operatorMap.get(boltId).getParallelism();
  }

  public OperatorDef getOperatorDef(String id) {
    return this.operatorMap.get(id);
  }

  public SourceDef getSourceDef(String id) {
    return this.sourceMap.get(id);
  }

  public SinkDef getSinkDef(String id) {
    return this.sinkMap.get(id);
  }

  public ComponentDef getComponent(String id) {
    return this.componentMap.get(id);
  }

  // used by includes implementation
  public void addAllOperators(List<OperatorDef> bolts, boolean override) {
    for (OperatorDef bolt : bolts) {
      String id = bolt.getId();
      if (this.operatorMap.get(id) == null || override) {
        this.operatorMap.put(bolt.getId(), bolt);
      } else {
        LOG.warn("Ignoring attempt to create operator '{}' with override == false.", id);
      }
    }
  }

  public void addAllSources(List<SourceDef> spouts, boolean override) {
    for (SourceDef spout : spouts) {
      String id = spout.getId();
      if (this.sourceMap.get(id) == null || override) {
        this.sourceMap.put(spout.getId(), spout);
      } else {
        LOG.warn("Ignoring attempt to create source '{}' with override == false.", id);
      }
    }
  }

  public void addAllSinks(List<SinkDef> sinks, boolean override) {
    for (SinkDef spout : sinks) {
      String id = spout.getId();
      if (this.sinkMap.get(id) == null || override) {
        this.sinkMap.put(spout.getId(), spout);
      } else {
        LOG.warn("Ignoring attempt to create sink '{}' with override == false.", id);
      }
    }
  }

  public void addAllComponents(List<ComponentDef> components, boolean override) {
    for (ComponentDef bean : components) {
      String id = bean.getId();
      if (this.componentMap.get(id) == null || override) {
        this.componentMap.put(bean.getId(), bean);
      } else {
        LOG.warn("Ignoring attempt to create component '{}' with override == false.", id);
      }
    }
  }

  public void addAllStreams(List<StreamDef> streams, boolean override) {
    //TODO figure out how we want to deal with overrides. Users may want to add streams even when overriding other
    // properties. For now we just add them blindly which could lead to a potentially invalid topology.
    this.streams.addAll(streams);
  }

  /**
   * validate that the definition actually represents a valid Flux topology.
   * @return true if the topology def is valid.
   */
  public boolean validate() {
    boolean hasSources = this.sourceMap != null && this.sourceMap.size() > 0;
    boolean hasSinks = this.sinkMap != null && this.sinkMap.size() > 0;
    boolean hasStreams = this.streams != null && this.streams.size() > 0;
    if (hasSources && hasSinks && hasStreams) {
      return true;
    }
    return true;
  }
}
