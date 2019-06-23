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

package com.uber.athena.flux.converter.runtime.traverser;

import com.uber.athena.flux.converter.api.converter.Converter;
import com.uber.athena.flux.converter.api.node.BaseNode;
import com.uber.athena.flux.converter.api.node.Node;
import com.uber.athena.flux.converter.api.traverser.Traverser;
import com.uber.athena.flux.model.SourceDef;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

/**
 * This traverser program traverse the topology DAG exactly once in BFS manner.
 *
 * <p>The traverse depends on {@link BaseTraverserContext} to determine whether
 * a specific node is ready for processing or not.
 *
 * @param <IN> Converter input node type
 * @param <OUT> Converter output node type
 */
public class BfsTraverser<IN extends BaseNode, OUT extends Node> implements Traverser {

  protected final BaseTraverserContext<IN> context;
  protected final Converter<IN, OUT> converter;
  protected final Properties traverseProps;

  protected Queue<String> vertexTraverseQueue = new ArrayDeque<>();
  protected Map<String, Integer> upStreamConversionMap = new HashMap<>();

  public BfsTraverser(
      BaseTraverserContext<IN> context,
      Converter<IN, OUT> converter
  ) {
    this(context, converter, null);
  }

  public BfsTraverser(
      BaseTraverserContext<IN>  context,
      Converter<IN, OUT> converter,
      Properties traverseProps
  ) {
    this.context = context;
    this.converter = converter;
    this.traverseProps = traverseProps;
  }

  @Override
  public void validate() {
    // TODO(@walterddr) add validator API
  }

  @Override
  public void run() {
    // Setting up all vertex upstream conversion count
    for (IN node : context.getAllTraverseNodes()) {
      upStreamConversionMap.put(node.getVertexId(), node.getDownstreamVertexIds().size());
    }

    // Get all sources into queue.
    for (SourceDef source : context.getTopologyDef().getSources().values()) {
      vertexTraverseQueue.add(source.getId());
    }

    // Start BFS search and converter matching.
    while (!vertexTraverseQueue.isEmpty()) {

      // Convert the node from head of the queue.
      String vertexId = vertexTraverseQueue.element();
      IN node = context.getNode(vertexId);
      OUT convertedNode = converter.convert(node);

      converter.validate(convertedNode);

      // Add additional nodes to the queue if they are next to be convert.
      for (String downstreamId : node.getDownstreamVertexIds()) {
        Integer currentCount = upStreamConversionMap.get(downstreamId);
        upStreamConversionMap.put(downstreamId, currentCount - 1);
        if (currentCount - 1 == 0) {
          vertexTraverseQueue.add(downstreamId);
        }
      }
    }
  }
}
