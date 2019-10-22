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

package com.uber.athena.dsl.planner.flink.relation.rules.datastream.co;

import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.flink.relation.FlinkDataStreamRelationNode;
import com.uber.athena.dsl.planner.flink.relation.rules.datastream.BaseDataStreamRule;
import com.uber.athena.dsl.planner.model.StreamDef;
import com.uber.athena.dsl.planner.model.StreamSpecDef;
import com.uber.athena.dsl.planner.relation.RelationNode;
import com.uber.athena.dsl.planner.relation.rule.RuleCall;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;

import java.util.Iterator;
import java.util.Map;

/**
 * Create {@link DataStream} from any directly applicable co-process functions.
 *
 * <p>Rule covers all {@link org.apache.flink.api.common.functions.Function}
 * extensions that's directly applicable from the base
 * {@link org.apache.flink.streaming.api.datastream.ConnectedStreams}.
 */
public class DataStreamCoFunctionRule extends BaseDataStreamRule {

  @Override
  public RuleCall onMatch(RuleCall ruleCall) {
    Iterator<String> it = ruleCall.getUpstreamDefMapping().keySet().iterator();
    String dsId1 = it.next();
    String dsId2 = it.next();
    if (ruleCall.getUpstreamDefMapping().get(dsId1).getStreamSpec().getStreamType()
        == StreamSpecDef.StreamTypeEnum.FOLLOWING_CO_STREAM) {
      return process(ruleCall, dsId2, dsId1);
    } else {
      return process(ruleCall, dsId1, dsId2);
    }
  }

  @SuppressWarnings("unchecked")
  private RuleCall process(RuleCall ruleCall, String dsId1, String dsId2) {
    ElementNode elementNode = ruleCall.getElementNode();

    RelationNode upstream1 = ruleCall.getUpstreamRelationMapping().get(dsId1);
    DataStream<?> ds1 = upstream1.getRelation(ruleCall.getUpstreamDefMapping().get(dsId1));
    RelationNode upstream2 = ruleCall.getUpstreamRelationMapping().get(dsId2);
    DataStream<?> ds2 = upstream2.getRelation(ruleCall.getUpstreamDefMapping().get(dsId2));

    DataStream<?> resultDs = null;

    if (elementNode.getElement() instanceof KeyedCoProcessFunction) {
      resultDs = ds1.connect(ds2).process((KeyedCoProcessFunction) elementNode.getElement());
    }
    if (elementNode.getElement() instanceof CoProcessFunction) {
      resultDs = ds1.connect(ds2).process((CoProcessFunction) elementNode.getElement());
    } else if (elementNode.getElement() instanceof CoMapFunction) {
      resultDs = ds1.connect(ds2).map(elementNode.getElement());
    } else if (elementNode.getElement() instanceof CoFlatMapFunction) {
      resultDs = ds1.connect(ds2).flatMap(elementNode.getElement());
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
    if (upstreamDefMapping.size() != 2) {
      return false;
    }
    Iterator<StreamDef> it = upstreamDefMapping.values().iterator();
    StreamSpecDef.StreamTypeEnum type1 = it.next().getStreamSpec().getStreamType();
    StreamSpecDef.StreamTypeEnum type2 = it.next().getStreamSpec().getStreamType();

    // Return true only when lead and follow stream config matches requirement.
    return (type1 == StreamSpecDef.StreamTypeEnum.LEADING_CO_STREAM
        && type2 == StreamSpecDef.StreamTypeEnum.FOLLOWING_CO_STREAM)
        || (type1 == StreamSpecDef.StreamTypeEnum.FOLLOWING_CO_STREAM
        && type2 == StreamSpecDef.StreamTypeEnum.LEADING_CO_STREAM);
  }
}
