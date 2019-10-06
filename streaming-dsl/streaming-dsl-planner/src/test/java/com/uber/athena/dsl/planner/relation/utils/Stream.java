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
 * A stream constructs for testing relation builder purpose.
 *
 * <p>With an operator, its associated upstream connections: {@link StreamDef}
 * and the {@link Stream}.
 */
public interface Stream {

  /**
   * get the upstream connection definition.
   *
   * @return the stream/connection definitions.
   */
  List<StreamDef> getUpstreamDefs();

  /**
   * get the upstreams.
   *
   * @return the upstream.
   */
  Map<String, Stream> getUpstreams();

  /**
   * get the operator associated with this stream.
   *
   * @return the operator.
   */
  BaseOperator getOperator();
}
