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

package com.uber.athena.flux.converter.runtime.converter;

import com.uber.athena.flux.converter.api.converter.BlackBoard;
import com.uber.athena.flux.converter.api.converter.ConverterContext;
import com.uber.athena.flux.converter.api.traverser.TraverserContext;

import java.util.HashMap;
import java.util.Map;

public class RuleSetConverterContext<T> implements ConverterContext {

  protected final Map<String, BlackBoard> blackboardMap = new HashMap<>();
  protected final Map<String, T> convertedNodeMap = new HashMap<>();
  private TraverserContext traverserContext;

  public RuleSetConverterContext(
      TraverserContext traverserContext) {
    this.traverserContext = traverserContext;
  }

  public BlackBoard getBlackboard(String nodeBlackboardId) {
    return blackboardMap.get(nodeBlackboardId);
  }

  public <R> R getBlackboardNode(String blackboardId, String vertexId, Class<R> nodeClass) {
    return blackboardMap.get(blackboardId).getNode(vertexId, nodeClass);
  }

  public T getConvertedNode(String vertexId) {
    return convertedNodeMap.get(vertexId);
  }

  @Override
  public TraverserContext getTraverserContext() {
    return traverserContext;
  }
}
