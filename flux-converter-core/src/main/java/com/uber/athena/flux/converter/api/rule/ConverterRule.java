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
import com.uber.athena.flux.converter.api.node.Node;
import com.uber.athena.flux.converter.api.traverser.TraverserContext;

public abstract class ConverterRule implements Rule {

  protected final String description;
  protected final Class<? extends Node> inClazz;
  protected final Class<? extends Node> outClazz;

  /**
   * Creates a <code>ConverterRule</code>.
   *
   * @param inClazz     Type of node clazz to consider converting
   * @param outClazz    Type of node clazz converting to.
   * @param description Description of rule
   */
  public <IN extends Node, OUT extends Node> ConverterRule(
      Class<IN> inClazz,
      Class<OUT> outClazz,
      String description) {
    this.description = description == null
        ? "ConverterRule<in=" + inClazz + ",out=" + outClazz + ">"
        : description;
    this.inClazz = inClazz;
    this.outClazz = outClazz;
  }

  /**
   * Deteremine whether a specific {@link Node} matches the firing criteria.
   *
   * @param converterRuleOpt the converter rule operator
   * @param traverserContext traverser context
   * @param converterContext converter context
   * @return true if this rule can be fired with the operator setup.
   */
  @Override
  public boolean matches(
      RuleOpt converterRuleOpt,
      TraverserContext traverserContext,
      ConverterContext converterContext) {
    Node node = converterRuleOpt.getNode();
    Class nodeClass = node.getClass();
    return inClazz.isAssignableFrom(nodeClass);
  }

  public String getDescription() {
    return description;
  }

  public Class<?> getInputClass() {
    return inClazz;
  }

  public Class<?> getTransformedClass() {
    return outClazz;
  }
}
