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

package com.uber.athena.dsl.planner.flink.relation.rules.datastream.sink;

import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.flink.relation.FlinkDataStreamRelationNode;
import com.uber.athena.dsl.planner.flink.relation.rules.datastream.BaseDataStreamRule;
import com.uber.athena.dsl.planner.model.StreamDef;
import com.uber.athena.dsl.planner.relation.RelationNode;
import com.uber.athena.dsl.planner.relation.rule.RuleCall;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.Map;

/**
 * Rule to create a {@link DataStreamSink} from a {@link SinkFunction}.
 */
public class SinkFunctionRule extends BaseDataStreamRule {

  @Override
  public RuleCall onMatch(RuleCall ruleCall) {
    ElementNode elementNode = ruleCall.getElementNode();
    String upstreamId = ruleCall.getUpstreamDefMapping().keySet().iterator().next();
    RelationNode upstream = ruleCall.getUpstreamRelationMapping().get(upstreamId);
    DataStream<?> ds = upstream.getRelation(ruleCall.getUpstreamDefMapping().get(upstreamId));
    DataStreamSink<?> resultDs = null;

    if (elementNode.getElement() instanceof SinkFunction) {
      resultDs = ds.addSink(elementNode.getElement());
    }

    if (resultDs != null) {
      ruleCall.setRelationNode(
          new FlinkDataStreamRelationNode(ruleCall, resultDs, DataStreamSink.class));
    }
    return ruleCall;
  }

  @Override
  public boolean matches(RuleCall ruleCall) {
    Map<String, StreamDef> upstreamDefMapping = ruleCall.getUpstreamDefMapping();
    return upstreamDefMapping.size() == 1
        && (ruleCall.getElementNode().getElement() instanceof SinkFunction);
  }
}
