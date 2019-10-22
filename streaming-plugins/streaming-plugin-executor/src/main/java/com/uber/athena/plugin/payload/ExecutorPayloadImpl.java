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

package com.uber.athena.plugin.payload;

import com.uber.athena.plugin.api.ExecutorPayload;
import com.uber.athena.plugin.api.Plugin;
import com.uber.athena.plugin.api.PluginPayload;
import com.uber.athena.plugin.utils.SerializationUtils;

/**
 * The is a wrapper class for creating a wrapper of {@link PluginPayload}.
 */
@SuppressWarnings("unchecked")
public class ExecutorPayloadImpl implements ExecutorPayload {
  private static final long serialVersionUID = -1;
  private final String pluginClazz;
  private final PluginPayload payload;

  public ExecutorPayloadImpl(String pluginClazz, PluginPayload payload) {
    this.pluginClazz = pluginClazz;
    this.payload = payload;
  }

  @Override
  public Class<? extends Plugin> getPluginClazz() {
    try {
      return (Class<? extends Plugin>) Class.forName(pluginClazz);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("cannot found plugin class object for: " + pluginClazz, e);
    }
  }

  @Override
  public PluginPayload getPluginPayload() {
    return payload;
  }

  @Override
  public byte[] serialize() throws Exception {
    return SerializationUtils.serializerJavaObj(this);
  }
}
