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

package com.uber.athena.flux.converter.impl.traverser;

import com.uber.athena.flux.converter.api.converter.Converter;
import com.uber.athena.flux.converter.api.converter.ConverterContext;
import com.uber.athena.flux.converter.api.rule.ConverterRule;
import com.uber.athena.flux.converter.api.rule.RuleSet;
import com.uber.athena.flux.converter.impl.testutils.converter.SimpleConverter;
import com.uber.athena.flux.converter.impl.testutils.converter.SimpleConverterContext;
import com.uber.athena.flux.model.TopologyDef;
import com.uber.athena.flux.parser.FluxParser;
import org.junit.Test;

import java.util.List;

public abstract class TraverseTestBase {

  private final List<String> testTopologies;
  private final List<RuleSet<ConverterRule>> ruleSets;

  protected TraverseTestBase(
      List<String> testTopologies,
      List<RuleSet<ConverterRule>> ruleSets) {
    this.testTopologies = testTopologies;
    this.ruleSets = ruleSets;
  }

  @Test
  public void testTopologies() throws Exception {
    for (String resource : testTopologies) {
      for (RuleSet<ConverterRule> ruleSet : ruleSets) {
        TopologyDef topologyDef = FluxParser.parseResource(resource,
            false, true, null, false);
        traverserPassthroughValidation(topologyDef, ruleSet);
      }
    }
  }

  private void traverserPassthroughValidation(
      TopologyDef topologyDef,
      RuleSet<ConverterRule> ruleSet) throws Exception {
    BaseTraverserContext traverserCtx = new BaseTraverserContext(topologyDef);
    ConverterContext converterCtx = new SimpleConverterContext();
    Converter converter = new SimpleConverter(ruleSet);
    BaseTraverser traverser = new BaseTraverser(
        traverserCtx, converterCtx, converter
    );

    traverser.run();
    traverser.validate();
  }
}
