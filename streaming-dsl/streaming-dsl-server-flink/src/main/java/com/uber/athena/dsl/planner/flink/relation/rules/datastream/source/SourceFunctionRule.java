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

package com.uber.athena.dsl.planner.flink.relation.rules.datastream.source;

import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.flink.relation.FlinkDataStreamRelationNode;
import com.uber.athena.dsl.planner.flink.relation.rules.datastream.BaseDataStreamRule;
import com.uber.athena.dsl.planner.flink.type.FlinkType;
import com.uber.athena.dsl.planner.model.StreamDef;
import com.uber.athena.dsl.planner.relation.rule.RuleCall;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Map;

/**
 * Rule to create a {@link DataStreamSource} from a {@link SourceFunction}.
 */
public class SourceFunctionRule extends BaseDataStreamRule {

  @Override
  public RuleCall onMatch(RuleCall ruleCall) {
    ElementNode elementNode = ruleCall.getElementNode();
    DataStreamSource<?> resultDs = null;

    if (elementNode.getElement() instanceof SourceFunction) {
      if (elementNode.getProduceType() != null) {
        FlinkType type = elementNode.getProduceType();
        resultDs = this.getStreamEnv().addSource(
            elementNode.getElement(),
            (TypeInformation<?>) type.getTypeInformation());
      } else {
        resultDs = this.getStreamEnv().addSource(elementNode.getElement());
      }
    }

    if (resultDs != null) {
      ruleCall.setRelationNode(
          new FlinkDataStreamRelationNode(ruleCall, resultDs, DataStreamSource.class));
    }
    return ruleCall;
  }

  @Override
  public boolean matches(RuleCall ruleCall) {
    Map<String, StreamDef> upstreamDefMapping = ruleCall.getUpstreamDefMapping();
    return upstreamDefMapping.size() == 0
        && (ruleCall.getElementNode().getElement() instanceof SourceFunction);
  }
}
