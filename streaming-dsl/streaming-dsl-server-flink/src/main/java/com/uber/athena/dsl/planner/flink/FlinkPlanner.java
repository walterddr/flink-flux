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

package com.uber.athena.dsl.planner.flink;

import com.uber.athena.dsl.planner.Planner;
import com.uber.athena.dsl.planner.element.ElementBuilder;
import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.element.constructor.Constructor;
import com.uber.athena.dsl.planner.flink.element.constructor.FlinkConstructor;
import com.uber.athena.dsl.planner.flink.type.FlinkTypeFactory;
import com.uber.athena.dsl.planner.parser.DslParser;
import com.uber.athena.dsl.planner.parser.Parser;
import com.uber.athena.dsl.planner.relation.RelationBuilder;
import com.uber.athena.dsl.planner.relation.RelationNode;
import com.uber.athena.dsl.planner.relation.rule.RuleExecutor;
import com.uber.athena.dsl.planner.relation.rule.RuleExecutorImpl;
import com.uber.athena.dsl.planner.relation.rule.RuleSet;
import com.uber.athena.dsl.planner.topology.DslTopologyBuilder;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.type.TypeFactory;
import com.uber.athena.dsl.planner.utils.ConstructionException;
import com.uber.athena.dsl.planner.utils.ParsingException;
import com.uber.athena.dsl.planner.utils.ValidationException;
import com.uber.athena.dsl.planner.validation.DslValidator;
import com.uber.athena.dsl.planner.validation.Validator;
import org.apache.flink.configuration.Configuration;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * The planner implementation specific to Flink runtime environment.
 *
 * <p>The extended components constructs necessary Flink runtime environment
 * based on the configurations.
 */
public class FlinkPlanner implements Planner {

  private final Parser parser;
  private final Validator validator;
  private final ElementBuilder elementBuilder;
  private final RelationBuilder relationBuilder;
  private final Configuration flinkConf;
  private final Map<String, Object> config;
  private final Properties properties;

  protected FlinkPlanner(
      Parser parser,
      Validator validator,
      ElementBuilder elementBuilder,
      RelationBuilder relationBuilder,
      Configuration flinkConf,
      Map<String, Object> config,
      Properties properties) {
    this.parser = parser;
    this.validator = validator;
    this.elementBuilder = elementBuilder;
    this.relationBuilder = relationBuilder;
    this.flinkConf = flinkConf;
    this.config = config;
    this.properties = properties;
  }

  @Override
  public Topology parse(InputStream stream) throws ParsingException {
    // fix config utilization.
    return parser.parseInputStream(stream, false, null, false);
  }

  @Override
  public Topology validate(Topology topology) throws ValidationException {
    return validator.validate(topology);
  }

  @Override
  public Map<String, ? extends ElementNode> constructElement(
      Topology topology) throws ConstructionException {
    return elementBuilder.construct(topology);
  }

  @Override
  public Map<String, ? extends RelationNode> constructRelation(
      Topology topology,
      Map<String, ElementNode> elementMapping) throws ConstructionException {
    return relationBuilder.construct(topology, elementMapping);
  }

  /**
   * Builder pattern for constructing a {@link FlinkPlanner}.
   *
   * <p>The builder pattern does not currently allow users to extend with
   * special impl of the planner components yet. It only allows users to
   * provide the configs, properties and the {@link Configuration}.
   */
  public static class Builder {
    private Parser parser;
    private Validator validator;
    private ElementBuilder elementBuilder;
    private RelationBuilder relationBuilder;

    private TypeFactory typeFactory;
    private Constructor constructor;
    private RuleExecutor ruleExecutor;
    private DslTopologyBuilder topologyBuilder;

    private RuleSet ruleSet;
    private Configuration flinkConf;
    private Map<String, Object> config;
    private Properties properties;

    public Builder() {
    }

    public Builder flinkConf(Configuration flinkConf) {
      this.flinkConf = flinkConf;
      return this;
    }

    public Builder config(Map<String, Object> config) {
      this.config = config;
      return this;
    }

    public Builder properties(Properties properties) {
      this.properties = properties;
      return this;
    }

    public Builder ruleSet(RuleSet ruleSet) {
      this.ruleSet = ruleSet;
      return this;
    }

    public FlinkPlanner build() {
      this.constructor = new FlinkConstructor();
      this.typeFactory = new FlinkTypeFactory();
      this.ruleExecutor = new RuleExecutorImpl(ruleSet, config);
      this.topologyBuilder = new DslTopologyBuilder();

      this.parser = new DslParser(config, topologyBuilder);
      this.validator = new DslValidator();
      this.elementBuilder = new ElementBuilder(config, constructor, typeFactory);
      this.relationBuilder = new RelationBuilder(config, ruleExecutor);

      return new FlinkPlanner(
          parser,
          validator,
          elementBuilder,
          relationBuilder,
          flinkConf,
          config,
          properties);
    }
  }
}
