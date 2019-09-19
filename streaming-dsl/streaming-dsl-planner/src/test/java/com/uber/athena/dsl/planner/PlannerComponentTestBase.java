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

package com.uber.athena.dsl.planner;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

/**
 * Base test class for a specific component within a planner.
 */
public abstract class PlannerComponentTestBase {
  private static final Logger LOG = LoggerFactory.getLogger(PlannerComponentTestBase.class);
  private static final String DEFAULT_TEST_RESOURCE_PATH = "dsl/";

  /*
   TODO @walterddr add following test cases
   1. env sub
   2. property sub
   3. references and includes
   4. dynamic classloadering
   */

  /*
   TODO @walterddr add more testing functionalities
   1. test with failure
   2. test with exceptions
   3. test with validation (by loading proper files (based on resource name))
   */

  /*
   TODO @walterddr create testing framework to:
   - invoke external validation mechanism to check the Topology content.
   */

  @Test
  public void testPlannerComponent() throws Exception {
    File[] testFiles = this.getTestTopologies();
    for (File file : testFiles) {
      LOG.info("Testing: " + file.getCanonicalPath());
      testTopology(file);
    }
  }

  public abstract void testTopology(File file) throws Exception;

  public File[] getTestTopologies() {
    return getResourceFolderFiles(DEFAULT_TEST_RESOURCE_PATH);
  }

  private static File[] getResourceFolderFiles(String folder) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL url = loader.getResource(folder);
    String path = url.getPath();
    return new File(path).listFiles();
  }
}
