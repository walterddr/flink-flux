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
 * Encapsulates the payload used by a specific {@link Plugin}.
 *
 * @param <T> the type of the concrete plugin payload implementation.
 */
public interface PluginPayload<T extends PluginPayload> extends Serializable {

  /**
   * Serialize the plugin payload into a byte array.
   *
   * @return the serialized byte array.
   * @throws Exception when serialization exception occurs.
   */
  byte[] serialize() throws Exception;

  /**
   * Deserialize the payload byte array back to the plugin payload.
   *
   * @param serializedObj serialized object obtained from the serialize() API.
   * @return the Java object of the plugin payload.
   * @throws Exception when deserialization exception occurs.
   */
  T deserialize(byte[] serializedObj) throws Exception;
}
