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

package com.uber.athena.dsl.planner.relation.utils;

import com.uber.athena.dsl.planner.element.utils.BaseOperator;
import com.uber.athena.dsl.planner.model.StreamDef;

import java.util.List;
import java.util.Map;

/**
 * A basic definition of a {@link Stream}.
 *
 * @param <T> extended type of base operator.
 */
public class BaseStream<T extends BaseOperator> implements Stream {

  private T operator;
  private List<StreamDef> streamDefs;
  private Map<String, Stream> upstreams;

  public BaseStream(
      T operator,
      List<StreamDef> streamDefs,
      Map<String, Stream> upstreams) {
    this.operator = operator;
    this.streamDefs = streamDefs;
    this.upstreams = upstreams;
  }

  @Override
  public List<StreamDef> getUpstreamDefs() {
    return streamDefs;
  }

  @Override
  public Map<String, Stream> getUpstreams() {
    return upstreams;
  }

  @Override
  public T getOperator() {
    return operator;
  }
}
