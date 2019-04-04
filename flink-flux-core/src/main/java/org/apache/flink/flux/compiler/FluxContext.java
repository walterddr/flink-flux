package org.apache.flink.flux.compiler;

import org.apache.flink.api.common.operators.Operator;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.flux.model.TopologyDef;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.operators.StreamOperator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluxContext {
  // parsed Topology definition
  TopologyDef topologyDef;

  // Storm config
  private Configuration config;

  // components required to be instantiated from classpath JARs
  private List<Object> additionalComponents;

  /**
   * The following are materialized objects from the {@link TopologyDef}.
   */
  private Map<String, Object> componentMap = new HashMap<String, Object>();

  private Map<String, DataStreamSource<?>> dataStreamSourceMap = new HashMap<String, DataStreamSource<?>>();

  private Map<String, StreamOperator<?>> operatorMap = new HashMap<String, StreamOperator<?>>();

  private Map<String, DataStreamSink<?>> dataStreamSinkMap = new HashMap<String, DataStreamSink<?>>();

  /**
   * The following is used by {@link CompilationGraph}.
   */
  private Map<String, CompilationVertex> compilationVertexMap = new HashMap<>();

  public FluxContext(TopologyDef topologyDef, Configuration config){
    this.topologyDef = topologyDef;
    this.config = config;
  }

  public TopologyDef getTopologyDef(){
    return this.topologyDef;
  }

  public void addSource(String id, DataStreamSource<?> source){
    this.dataStreamSourceMap.put(id, source);
  }

  public void addSink(String id, DataStreamSink<?> sink){
    this.dataStreamSinkMap.put(id, sink);
  }

  public void addOperator(String id, StreamOperator<?> op){
    this.operatorMap.put(id, op);
  }

  public void addComponent(String id, Object value){
    this.componentMap.put(id, value);
  }

  public Object getComponent(String id){
    return this.componentMap.get(id);
  }

  public void putCompilationVertex(String key, CompilationVertex value) {
    compilationVertexMap.put(key, value);
  }

  public CompilationVertex getCompilationVertex(String key) {
    return compilationVertexMap.get(key);
  }
}
