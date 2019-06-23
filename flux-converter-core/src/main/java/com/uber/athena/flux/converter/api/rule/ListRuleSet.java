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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ListRuleSet implements RuleSet {

  private final List<Rule> rules;

  ListRuleSet(List<Rule> rules) {
    this.rules = rules;
  }

  @Override
  public int hashCode() {
    return rules.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this
        || obj instanceof ListRuleSet
        && rules.equals(((ListRuleSet) obj).rules);
  }

  @Override
  public Iterator<Rule> iterator() {
    return rules.iterator();
  }

  /** Creates a rule set with a given array of rules. */
  public static RuleSet ofList(Rule... rules) {
    return new ListRuleSet(Arrays.asList(rules));
  }

  /** Creates a rule set with a given array of rules. */
  public static RuleSet ofList(Collection<Rule> rules) {
    return new ListRuleSet(new ArrayList<>(rules));
  }
}
