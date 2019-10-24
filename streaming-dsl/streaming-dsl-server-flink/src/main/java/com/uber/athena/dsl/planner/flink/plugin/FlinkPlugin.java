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

package com.uber.athena.dsl.planner.flink.plugin;

import com.uber.athena.dsl.planner.flink.FlinkPlanner;
import com.uber.athena.dsl.planner.flink.relation.rules.FlinkRuleSet;
import com.uber.athena.dsl.planner.flink.relation.rules.datastream.FlinkDataStreamRuleSet;
import com.uber.athena.dsl.planner.flink.relation.rules.table.FlinkTableRuleSet;
import com.uber.athena.plugin.api.Plugin;
import com.uber.athena.plugin.api.PluginPayload;
import org.apache.flink.configuration.Configuration;

/**
 * Base class to provide DSL construction via {@link FlinkPlanner}.
 */
public abstract class FlinkPlugin implements Plugin {
  protected FlinkPluginPayload payload;
  protected FlinkPlanner planner;
  protected FlinkRuleSet ruleSet;

  @Override
  public void instantiate(PluginPayload pluginPayload) {
    if (!(pluginPayload instanceof FlinkPluginPayload)) {
      throw new IllegalArgumentException("Cannot instantiate Flink plugin "
          + "with given payload: " + pluginPayload.getClass());
    }
    this.payload = (FlinkPluginPayload) pluginPayload;
    this.ruleSet = getStandardRuleSet(payload.getRuleSetType());
    this.planner = new FlinkPlanner.Builder()
        .config(payload.getConfig())
        .flinkConf(payload.getFlinkConf())
        .properties(payload.getProperties())
        .ruleSet(ruleSet)
        .build();
  }

  /**
   * mechanism to resolve standard rule set.
   *
   * <p>Due to the fact that many rules cannot be serialized and rulesets are
   * rather static, we allow users to override this method to construct a
   * standard ruleset based on plugin environment.
   *
   * @param ruleSetType ruleSetType enum
   * @return the desired rule set.
   */
  protected FlinkRuleSet getStandardRuleSet(FlinkPluginPayload.PluginRuleSetType ruleSetType) {
    // TODO @walterddr fix config handling (#30)
    Integer parallelism = Integer.parseInt(
        (String) this.payload.getConfig().get("_JOB_PARALLELISM"));
    Configuration flinkConf = payload.getFlinkConf();
    switch (ruleSetType) {
      case DATASTREAM:
        return new FlinkDataStreamRuleSet(parallelism, flinkConf);
      case TABLE:
        return new FlinkTableRuleSet(parallelism, flinkConf);
      default:
        throw new UnsupportedOperationException("Unsupported ruleSetType: " + ruleSetType);
    }
  }
}
