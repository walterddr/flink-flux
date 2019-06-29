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

import com.uber.athena.flux.converter.api.converter.ConverterContext;
import com.uber.athena.flux.converter.api.node.Node;
import com.uber.athena.flux.converter.api.rule.Rule;
import com.uber.athena.flux.converter.api.traverser.TraverserContext;

import static com.uber.athena.flux.converter.api.rule.ListRuleSet.ofList;

public class ExampleConverter extends RuleSetConverter {

  public ExampleConverter(Rule... rules) {
    this.converterRuleSet = ofList(rules);
  }

  @Override
  public void convert(Node node, TraverserContext traverserContext, ConverterContext converterContext) {
    // For each node, exhaust all the rules that matches (IN ORDER)
    // until no matches, then move on to next traverse.
    for (Rule rule: converterRuleSet) {
      if (rule.matches(node)) {
        rule.onMatch(node);
      }

    }
  }

  @Override
  public void validate(Node node, TraverserContext traverserContext, ConverterContext converterContext) {
    // TODO(@walterddr) add this.
  }
}
