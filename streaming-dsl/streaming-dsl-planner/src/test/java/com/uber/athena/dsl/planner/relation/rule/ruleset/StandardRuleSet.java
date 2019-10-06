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

package com.uber.athena.dsl.planner.relation.rule.ruleset;

import com.uber.athena.dsl.planner.relation.rule.OneInputConstructRule;
import com.uber.athena.dsl.planner.relation.rule.Rule;
import com.uber.athena.dsl.planner.relation.rule.RuleSet;
import com.uber.athena.dsl.planner.relation.rule.SourceConstructRule;
import com.uber.athena.dsl.planner.relation.rule.TwoInputConstructRule;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Standard test rule set.
 */
public class StandardRuleSet implements RuleSet {

  private static final List<Rule> RULES = Arrays.asList(
      new SourceConstructRule(),
      new OneInputConstructRule(),
      new TwoInputConstructRule()
  );
  private static final StandardRuleSet INSTANCE = new StandardRuleSet();

  @Override
  public Iterator<Rule> iterator() {
    return RULES.iterator();
  }

  public static StandardRuleSet getInstance() {
    return INSTANCE;
  }
}
