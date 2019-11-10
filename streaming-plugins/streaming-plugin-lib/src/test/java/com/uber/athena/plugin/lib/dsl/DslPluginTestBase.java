/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.uber.athena.plugin.lib.dsl;

import com.uber.athena.dsl.planner.flink.DslTestBase;
import com.uber.athena.plugin.lib.dsl.payload.FlinkPluginPayload;
import org.apache.flink.configuration.Configuration;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Base class for DSL plugin test.
 */
@RunWith(Parameterized.class)
public abstract class DslPluginTestBase extends DslTestBase {

  protected DslPluginTestBase(String name, File file) {
    super(name, file);
  }

  protected FlinkPluginPayload generatePayload() {
    Configuration flinkConf = new Configuration();
    // default configuration & properties for instantiate the planner.
    Properties properties = new Properties();
    // TODO @walterddr fix config handling (#30)
    Map<String, Object> config = Collections.singletonMap("_JOB_PARALLELISM", "1");
    return new FlinkPluginPayload(
        file.getAbsolutePath(),
        FlinkPluginPayload.PluginRuleSetType.DATASTREAM,
        flinkConf,
        config,
        properties
    );
  }

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    File[] testFiles = getResourceFolderFiles(DEFAULT_TEST_DSL_MODEL_PATH);

    Collection<Object[]> data = new ArrayList<>();
    for (File testFile : testFiles) {
      data.add(new Object[]{testFile.getName(), testFile});
    }
    return data;
  }
}
