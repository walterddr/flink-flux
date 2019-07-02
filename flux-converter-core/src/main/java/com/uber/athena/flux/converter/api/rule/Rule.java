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

package com.uber.athena.flux.converter.api.rule;

import com.uber.athena.flux.converter.api.converter.ConverterContext;
import com.uber.athena.flux.converter.api.traverser.TraverserContext;

/**
 * Defines a converter rule that transforms nodes from one type to another.
 */
public interface Rule {

  /**
   * Determine whether a {@link RuleOpt} matches the firing criteria.
   *
   * @param ruleOpt rule operator object
   * @param traverserContext traverser context
   * @param converterContext converter context
   * @return true if this rule can be fired with the node setup.
   */
  boolean matches(
      RuleOpt ruleOpt,
      TraverserContext traverserContext,
      ConverterContext converterContext);

  /**
   * Receives notification about a rule match.
   *
   * <p>At the time that this method is called, {@link RuleOpt} holds
   * the Node that matches the rule.
   *
   * <p>Typically a rule would create a {@link RuleOpt} with the context;
   * check that the nodes are valid matches; configure the opt properly; then
   * calls back {@link RuleOpt.transform} to register the expression.
   *
   * @param ruleOpt rule operator object
   * @param traverserContext the traverse context
   * @param converterContext the converter context
   */
  void onMatch(
      RuleOpt ruleOpt,
      TraverserContext traverserContext,
      ConverterContext converterContext);
}
