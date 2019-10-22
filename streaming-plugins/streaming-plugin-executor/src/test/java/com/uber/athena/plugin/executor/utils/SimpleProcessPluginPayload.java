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

package com.uber.athena.plugin.executor.utils;

import com.uber.athena.plugin.api.PluginPayload;
import com.uber.athena.plugin.utils.SerializationUtils;

/**
 * Test {@link PluginPayload}.
 */
public class SimpleProcessPluginPayload implements PluginPayload<SimpleProcessPluginPayload> {

  private final String myStrField;
  private final Integer myIntField;

  public SimpleProcessPluginPayload(String myStrField, Integer myIntField) {
    this.myStrField = myStrField;
    this.myIntField = myIntField;
  }

  @Override
  public byte[] serialize() throws Exception {
    return SerializationUtils.serializerJavaObj(this);
  }

  @Override
  public SimpleProcessPluginPayload deserialize(byte[] serializedObj) throws Exception {
    return SerializationUtils.javaDeserialize(serializedObj);
  }

  public String getMyStrField() {
    return myStrField;
  }

  public Integer getMyIntField() {
    return myIntField;
  }
}
