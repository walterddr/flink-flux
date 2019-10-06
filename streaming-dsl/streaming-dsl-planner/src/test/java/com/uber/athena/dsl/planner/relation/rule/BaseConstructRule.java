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

package com.uber.athena.dsl.planner.relation.rule;

import com.uber.athena.dsl.planner.element.utils.BaseOperator;
import com.uber.athena.dsl.planner.model.StreamDef;
import com.uber.athena.dsl.planner.relation.BaseRelationNode;
import com.uber.athena.dsl.planner.relation.RelationNode;
import com.uber.athena.dsl.planner.relation.utils.BaseStream;
import com.uber.athena.dsl.planner.relation.utils.Stream;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple test {@link Rule} that construct a digest of the linked nodes.
 */
public abstract class BaseConstructRule implements Rule {

  @Override
  public RuleCall onMatch(RuleCall ruleCall) {
    BaseStream<BaseOperator> stream = constructBaseStream(
        ruleCall.getElementNode().getElement(),
        ruleCall.getUpstreamDefMapping(),
        ruleCall.getUpstreamRelationMapping());
    ruleCall.setRelationNode(
        new BaseRelationNode(ruleCall.getVertexId(), stream, BaseStream.class));
    return ruleCall;
  }

  protected <T extends BaseOperator> BaseStream<T> constructBaseStream(
      T operator,
      Map<String, StreamDef> streamDefs,
      Map<String, RelationNode> relations) {
    return new BaseStream<>(
        operator,
        new ArrayList<>(streamDefs.values()),
        relations
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> (Stream) relations.get(e.getKey()).getRelation(streamDefs.get(e.getKey())))));
  }
}
