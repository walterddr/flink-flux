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

import com.uber.athena.flux.converter.api.converter.Converter;
import com.uber.athena.flux.converter.api.converter.ConverterContext;
import com.uber.athena.flux.converter.api.node.dsl.DslNode;
import com.uber.athena.flux.converter.runtime.converter.ExampleConverter;
import com.uber.athena.flux.converter.runtime.converter.ExampleConverterContext;
import com.uber.athena.flux.model.TopologyDef;
import com.uber.athena.flux.parser.FluxParser;
import org.junit.Test;

import java.util.List;

public abstract class TraverseTestBase {

  private final List<String> testTopologies;

  protected TraverseTestBase(List<String> testTopologies) {
    this.testTopologies = testTopologies;
  }

  @Test
  public void testTopologies() throws Exception {
    for (String resource : testTopologies) {
      TopologyDef topologyDef = FluxParser.parseResource(resource,
          false, true, null, false);
      testTraversingWithEmptyRuleSet(topologyDef);
    }
  }

  private void testTraversingWithEmptyRuleSet(TopologyDef def) throws Exception {
    TopologyDef topologyDef = FluxParser.parseResource("/configs/simple_passthrough_topology.yaml",
        false, true, null, false);

    BaseTraverserContext<DslNode> traverserCtx = new ExampleTraverserContext(topologyDef);
    ConverterContext converterCtx = new ExampleConverterContext();
    Converter converter = new ExampleConverter();
    BfsTraverser<DslNode> traverser = new BfsTraverser<>(
        traverserCtx, converterCtx, converter
    );

    traverser.run();
  }
}
