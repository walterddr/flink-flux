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

/**
 * Encapsulates the execution result for a specific {@link Plugin}.
 *
 * <p>{@link PluginResult} is a special kind of {@link PluginPayload} that
 * carries the same serialization/deserialization methods. The difference
 * is that it can also carry exceptions along with the results generated.
 *
 * <p>Exceptions are parallel to the result/payload itself.
 *
 * @param <T> the type of plugin results supported
 */
public interface PluginResult<T extends PluginResult> extends PluginPayload<T> {

  /**
   * Accepts an exception as part of the plugin execution result.
   *
   * @param e the exception/throwable.
   */
  void setException(Throwable e);

  /**
   * Retrieves the exception that's earlier set.
   *
   * @return the exception/throwable.
   */
  Throwable getException();
}
