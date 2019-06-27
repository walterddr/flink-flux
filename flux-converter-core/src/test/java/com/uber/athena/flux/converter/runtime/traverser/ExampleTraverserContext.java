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

import com.uber.athena.flux.converter.api.node.dsl.DslNode;
import com.uber.athena.flux.converter.runtime.node.dsl.OperatorNode;
import com.uber.athena.flux.converter.runtime.node.dsl.SinkNode;
import com.uber.athena.flux.converter.runtime.node.dsl.SourceNode;
import com.uber.athena.flux.model.OperatorDef;
import com.uber.athena.flux.model.SinkDef;
import com.uber.athena.flux.model.SourceDef;
import com.uber.athena.flux.model.TopologyDef;
import com.uber.athena.flux.model.VertexDef;

import java.util.Properties;

public class ExampleTraverserContext extends BaseTraverserContext<DslNode> {

  public ExampleTraverserContext(TopologyDef topologyDef) {
    this(topologyDef, null);
  }

  public ExampleTraverserContext(TopologyDef topologyDef, Properties contextProperties) {
    super(topologyDef, contextProperties);
  }

  @Override
  public DslNode constructNode(String vertexId, VertexDef vertexDef) {
    if (vertexDef instanceof SourceDef) {
      return new SourceNode(vertexId, vertexDef);
    } else if (vertexDef instanceof SinkDef) {
      return new SinkNode(vertexId, vertexDef);
    } else if (vertexDef instanceof OperatorDef) {
      return new OperatorNode(vertexId, vertexDef);
    }
    return null;
  }
}
