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

package com.uber.athena.plugin.executor.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class consist of the general payload transfer utilities.
 *
 * <p>This is used to ensure that the {@link ProcessExecutor} and the
 * {@link PluginWrapperEntryPoint} utilizes the same form of serialization /
 * deserialization and same streams for data transfer between process.
 */
final class ProcessStreamTransferUtils {

  private ProcessStreamTransferUtils() {

  }

  /**
   * Instantiate the process launch ops.
   *
   * @param jvmVersionPath the version of JVM used.
   * @param jvmClassPath the class path used.
   * @param jvmLibPath the lib path used.
   * @return the list of argument used for a new JVM process.
   */
  public static List<String> buildProcessExecutorOps(
      String jvmVersionPath,
      String jvmClassPath,
      String jvmLibPath,
      int inStreamPort,
      int outStreamPort
  ) {
    ArrayList<String> ops = new ArrayList<>();
    // resolve local java launchable
    ops.add(resolveJvm(jvmVersionPath));

    // resolve class path
    ops.addAll(resolveClassPath(jvmClassPath));
    // resolve lib path
    ops.addAll(resolveLibPath(jvmLibPath));

    // execution main class
    ops.add(PluginWrapperEntryPoint.class.getCanonicalName());

    // add execution main class argument
    // These arguments should be used by pluginWrapper ONLY
    ops.add(Integer.toString(outStreamPort));
    ops.add(Integer.toString(inStreamPort));
    return ops;
  }

  private static String resolveJvm(String jvmVersionKey) {
    File jvm = null;
    if (jvmVersionKey != null) {
      jvm = new File(new File(jvmVersionKey, "bin"), "java");
      if (!jvm.exists()) {
        jvm = new File(new File(System.getProperty("java.home"), "bin"), "java");
      }
    }
    if (jvm == null || !jvm.exists()) {
      throw new IllegalArgumentException("Cannot find JVM executable! with key: " + jvmVersionKey);
    }
    return jvm.toString();
  }

  private static List<String> resolveClassPath(String jvmClassPath) {
    if (jvmClassPath != null) {
      if (new File(jvmClassPath).exists()) {
        return Arrays.asList("-classpath", jvmClassPath);
      }
      String javaClassPath = System.getProperty(jvmClassPath);
      if (javaClassPath != null) {
        return Arrays.asList("-classpath", javaClassPath);
      }
    }
    throw new IllegalArgumentException("Cannot find JVM classpath! with key: " + jvmClassPath);
  }

  private static List<String> resolveLibPath(String jvmLibPath) {
    if (jvmLibPath != null) {
      if (new File(jvmLibPath).exists()) {
        return Collections.singletonList("-Djava.library.path=" + jvmLibPath);
      }
      String javaLibPath = System.getProperty(jvmLibPath);
      if (javaLibPath != null) {
        return Collections.singletonList("-Djava.library.path=" + javaLibPath);
      }
    }
    return Collections.emptyList();
  }
}
