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

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.net.URL;

/**
 * Test of the generation of Flink runtime job graph / application.
 */
@RunWith(Parameterized.class)
public abstract class DslTestBase {
  protected static final String DEFAULT_TEST_DSL_MODEL_PATH = "dsl/";

  protected String name;
  protected File file;

  protected DslTestBase(String name, File file) {
    this.name = name;
    this.file = file;
  }

  protected static File[] getResourceFolderFiles(String folder) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL url = loader.getResource(folder);
    String path = url.getPath();
    return new File(path).listFiles();
  }
}
