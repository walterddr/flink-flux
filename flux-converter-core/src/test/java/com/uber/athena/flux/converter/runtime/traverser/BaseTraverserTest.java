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

import com.uber.athena.flux.converter.api.rule.ConverterRule;
import com.uber.athena.flux.converter.api.rule.ConverterRuleSet;
import com.uber.athena.flux.converter.api.rule.RuleSet;
import com.uber.athena.flux.converter.runtime.utils.rule.SimpleDslConversionRules;
import com.uber.athena.flux.converter.runtime.utils.rule.SimpleElementCreationRules;
import com.uber.athena.flux.converter.runtime.utils.rule.SimpleElementLinkageRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BaseTraverserTest extends TraverseTestBase {

  private static final List<String> TEST_TOPOLOGY_RESOURCE_PATHS = Arrays.asList(
      "/configs/simple_passthrough_topology.yaml",
      "/configs/diamond_topology.yaml");

  private static final List<RuleSet<ConverterRule>> RULE_SETS = Arrays.asList(
      ConverterRuleSet.ofList(Collections.emptyList()),
      ConverterRuleSet.ofList(
          SimpleDslConversionRules.OperatorConverter.INSTANCE,
          SimpleDslConversionRules.SourceConverter.INSTANCE,
          SimpleDslConversionRules.SinkConverter.INSTANCE,
          SimpleElementCreationRules.SourceCreationRule.INSTANCE,
          SimpleElementCreationRules.OneInputCreationRule.INSTANCE,
          SimpleElementCreationRules.TwoInputCreationRule.INSTANCE,
          SimpleElementLinkageRule.INSTANCE
      )
  );

  public BaseTraverserTest() {
    super(TEST_TOPOLOGY_RESOURCE_PATHS, RULE_SETS);
  }
}
