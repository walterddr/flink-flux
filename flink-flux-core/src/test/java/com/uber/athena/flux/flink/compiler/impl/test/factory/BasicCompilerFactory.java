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

package com.uber.athena.flux.flink.compiler.impl.test.factory;

import com.uber.athena.flux.flink.compiler.api.Compiler;
import com.uber.athena.flux.flink.compiler.api.CompilerFactory;
import com.uber.athena.flux.flink.compiler.impl.test.BasicCompilerImpl;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

public class BasicCompilerFactory implements CompilerFactory {

  private static final Compiler<DataStream> COMPILER = new BasicCompilerImpl();

  @Override
  public Compiler<?> getCompiler(Class<?> objectClass, Map<String, String> properties) {
    return COMPILER;
  }

  @Override
  public Compiler<?> getCompiler(String className, Map<String, String> properties) {
    return COMPILER;
  }
}
