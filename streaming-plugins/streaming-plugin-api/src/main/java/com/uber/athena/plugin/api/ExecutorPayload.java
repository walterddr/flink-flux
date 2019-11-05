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

package com.uber.athena.plugin.api;

import java.io.Serializable;

/**
 * Executor payload used for the executor to instantiate itself.
 */
public interface ExecutorPayload extends Serializable {

  /**
   * The Clazz of the plugin used for executing in the run command.
   *
   * @return the class of the plugin.
   */
  Class<? extends Plugin> getPluginClazz();

  /**
   * The payload passing to the {@link Plugin}.
   *
   * @return the plugin payload.
   */
  PluginPayload getPluginPayload();

  /**
   * Serialization method of the executor payload.
   *
   * <p>This functionality is useful when executing remotely.
   *
   * @return the serialized byte array.
   * @throws Exception when serialization fails.
   */
  byte[] serialize() throws Exception;
}
