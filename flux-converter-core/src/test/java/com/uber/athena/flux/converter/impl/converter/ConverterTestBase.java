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

package com.uber.athena.flux.converter.impl.converter;

import com.uber.athena.flux.converter.api.converter.Converter;
import com.uber.athena.flux.converter.api.converter.ConverterContext;
import com.uber.athena.flux.converter.api.node.expression.ExpressionNode;
import com.uber.athena.flux.converter.api.rule.ConverterRule;
import com.uber.athena.flux.converter.api.rule.RuleSet;
import com.uber.athena.flux.converter.impl.testutils.converter.SimpleConverter;
import com.uber.athena.flux.converter.impl.testutils.converter.SimpleConverterContext;
import com.uber.athena.flux.converter.impl.traverser.BaseTraverser;
import com.uber.athena.flux.converter.impl.traverser.BaseTraverserContext;
import com.uber.athena.flux.model.TopologyDef;
import com.uber.athena.flux.parser.FluxParser;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class ConverterTestBase {

  private static final String DEFAULT_ENCODING = "UTF8";
  private static final String DIGEST_MAP_KEY = "digestMap";
  private static final Yaml YAML_PARSER = new Yaml();

  private final List<String> testTopologies;
  private final List<String> expectedDigests;
  private final RuleSet<ConverterRule> ruleSet;

  protected ConverterTestBase(
      RuleSet<ConverterRule> ruleSet,
      List<String> testTopologies,
      List<String> expectedDigests) {
    this.ruleSet = ruleSet;
    this.testTopologies = testTopologies;
    this.expectedDigests = expectedDigests;
  }

  @Test
  public void testTopologies() throws Exception {
    assertEquals("test topology definition list and expected digest list should match size!",
        testTopologies.size(), expectedDigests.size());
    for (int idx = 0; idx < testTopologies.size(); idx++) {
      TopologyDef topologyDef = FluxParser.parseResource(testTopologies.get(idx),
          false, true, null, false);
      Map<String, String> digestMap = constructDigestMap(expectedDigests.get(idx));
      testConverterWithExpectedDigests(topologyDef, ruleSet, digestMap);
    }
  }

  @SuppressWarnings("unchecked")
  private static Map<String, String> constructDigestMap(String path) throws IOException {
    InputStream in = ConverterTestBase.class.getResourceAsStream(path);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    int b = -1;
    while ((b = in.read()) != -1) {
      bos.write(b);
    }

    String str = bos.toString(DEFAULT_ENCODING);
    Map<String, Map<String, String>> digestMap = (Map) YAML_PARSER.load(str);
    return digestMap.get(DIGEST_MAP_KEY);
  }

  private void testConverterWithExpectedDigests(
      TopologyDef topologyDef,
      RuleSet<ConverterRule> ruleSet,
      Map<String, String> expectedDigests) throws Exception {
    BaseTraverserContext traverserCtx = new BaseTraverserContext(topologyDef);
    ConverterContext converterCtx = new SimpleConverterContext();
    Converter converter = new SimpleConverter(ruleSet);
    BaseTraverser traverser = new BaseTraverser(
        traverserCtx, converterCtx, converter
    );

    // Run traverser to convert the entire topology
    traverser.run();
    traverser.validate();

    // For each expected digests key, find matching converted expression digests.
    for (Map.Entry<String, String> e : expectedDigests.entrySet()) {
      ExpressionNode node = converterCtx.getExpressionNode(e.getKey());
      assertNotNull(node);
      assertEquals(e.getValue(), node.getDigest());
    }
  }
}
