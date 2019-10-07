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

package com.uber.athena.dsl.planner.flink.relation.rules.datastream.base;

import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.flink.relation.FlinkDataStreamRelationNode;
import com.uber.athena.dsl.planner.flink.relation.rules.datastream.BaseDataStreamRule;
import com.uber.athena.dsl.planner.model.StreamDef;
import com.uber.athena.dsl.planner.model.StreamSpecDef;
import com.uber.athena.dsl.planner.relation.RelationNode;
import com.uber.athena.dsl.planner.relation.rule.RuleCall;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.ProcessFunction;

import java.util.Map;

/**
 * Create {@link DataStream} from any directly applicable functions.
 *
 * <p>Rule covers all {@link org.apache.flink.api.common.functions.Function}
 * extensions that's directly applicable from the base {@link DataStream}.
 */
public class DataStreamFunctionRule extends BaseDataStreamRule {

  @Override
  public RuleCall onMatch(RuleCall ruleCall) {
    ElementNode elementNode = ruleCall.getElementNode();
    String upstreamId = ruleCall.getUpstreamDefMapping().keySet().iterator().next();
    RelationNode upstream = ruleCall.getUpstreamRelationMapping().get(upstreamId);
    DataStream<?> ds = upstream.getRelation(ruleCall.getUpstreamDefMapping().get(upstreamId));
    DataStream<?> resultDs = null;

    if (elementNode.getElement() instanceof ProcessFunction) {
      resultDs = ds.process(elementNode.getElement());
    } else if (elementNode.getElement() instanceof MapFunction) {
      resultDs = ds.map(elementNode.getElement());
    } else if (elementNode.getElement() instanceof FlatMapFunction) {
      resultDs = ds.flatMap(elementNode.getElement());
    } else if (elementNode.getElement() instanceof FilterFunction) {
      resultDs = ds.filter(elementNode.getElement());
    }

    // if application success, assign relation node to ruleCall.
    if (resultDs != null) {
      ruleCall.setRelationNode(
          new FlinkDataStreamRelationNode(ruleCall, resultDs, DataStream.class));
    }
    return ruleCall;
  }

  @Override
  public boolean matches(RuleCall ruleCall) {
    Map<String, StreamDef> upstreamDefMapping = ruleCall.getUpstreamDefMapping();
    if (upstreamDefMapping.size() != 1) {
      return false;
    }
    StreamDef upstream = upstreamDefMapping.values().iterator().next();
    return upstream.getStreamSpec().getStreamType().equals(
        StreamSpecDef.StreamTypeEnum.DATA_STREAM);
  }
}
