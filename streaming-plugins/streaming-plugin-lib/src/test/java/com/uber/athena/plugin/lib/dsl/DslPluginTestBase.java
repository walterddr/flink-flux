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

import com.uber.athena.plugin.lib.dsl.payload.FlinkPluginPayload;
import org.apache.flink.configuration.Configuration;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Base class for DSL plugin test.
 */
abstract class DslPluginTestBase {
  private static final String TEST_DSL_FILE = "basic_topology.yaml";

  protected FlinkPluginPayload generatePayload() {
    Configuration flinkConf = new Configuration();
    Properties properties = new Properties();
    Map<String, Object> config = Collections.singletonMap("_JOB_PARALLELISM", "1");
    return new FlinkPluginPayload(
        Objects.requireNonNull(getResourceFile(TEST_DSL_FILE)).getAbsolutePath(),
        FlinkPluginPayload.PluginRuleSetType.DATASTREAM,
        flinkConf,
        config,
        properties
    );
  }

  protected static File getResourceFile(String file) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL url = loader.getResource(file);
    if (url != null) {
      return new File(url.getPath());
    } else {
      return null;
    }
  }
}
