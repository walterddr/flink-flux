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

package com.uber.athena.plugin.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Utility to serialize & deserialize object for remote execution transfer.
 */
@SuppressWarnings("unchecked")
public final class SerializationUtils {

  private SerializationUtils() {
  }

  public static byte[] serializerJavaObj(Object obj) {
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(obj);
      oos.close();
      return bos.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T javaDeserialize(byte[] serialized) {
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
      ObjectInputStream ois = null;
      ois = new ObjectInputStream(bis);
      Object ret = ois.readObject();
      ois.close();
      return (T) ret;
    } catch (IOException | ClassNotFoundException ioe) {
      throw new RuntimeException(ioe);
    }
  }
}
