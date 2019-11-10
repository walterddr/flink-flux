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

import com.uber.athena.plugin.api.ExecutorPayload;
import com.uber.athena.plugin.api.Plugin;
import com.uber.athena.plugin.api.PluginPayload;
import com.uber.athena.plugin.api.PluginResult;
import com.uber.athena.plugin.base.ExceptionPluginResult;
import com.uber.athena.plugin.payload.ExecutorPayloadImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * This wrapper is used to wrap around a {@link Plugin} for process execution.
 *
 * <p>The wrapper main class is meant to be execute by {@link ProcessExecutor}.
 */
public final class PluginWrapperEntryPoint {

  private PluginWrapperEntryPoint() {
  }

  public static void main(String[] args) throws Exception {
    PluginResult res;
    // execute the wrapped plugin
    try {
      ExecutorPayload executorPayload = decodeInputStream(chooseInputStream(args));
      PluginPayload payload = executorPayload.getPluginPayload();
      Class<?> pluginClazz = executorPayload.getPluginClazz();
      Plugin plugin = (Plugin) pluginClazz.getDeclaredConstructor().newInstance();
      plugin.instantiate(payload);
      res = plugin.run();
    } catch (Throwable e) {
      res = new ExceptionPluginResult(e);
    }

    // choose the correct output stream
    try (OutputStream out = chooseOutputStream(args)) {
      byte[] serializedResult = res.serialize();
      if (serializedResult != null) {
        out.write(serializedResult);
      }
    }
  }

  private static ExecutorPayloadImpl decodeInputStream(InputStream inputStream)
      throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(inputStream)) {
      return (ExecutorPayloadImpl) ois.readObject();
    }
  }

  private static InputStream chooseInputStream(String[] args) throws IOException {
    if (args.length <= 0) {
      throw new IllegalArgumentException("Cannot select input stream. Insufficient argument!");
    }
    int port = Integer.parseInt(args[0]);
    if (port > 0) {
      Socket sock = new Socket();
      sock.connect(new InetSocketAddress(InetAddress.getLocalHost(), port));
      return sock.getInputStream();
    } else {
      return System.in;
    }
  }

  private static OutputStream chooseOutputStream(String[] args) throws IOException {
    if (args.length <= 1) {
      throw new IllegalArgumentException("Cannot select output stream. Insufficient argument!");
    }
    int port = Integer.parseInt(args[1]);
    if (port > 0) {
      Socket sock = new Socket();
      sock.connect(new InetSocketAddress(InetAddress.getLocalHost(), port));
      return sock.getOutputStream();
    } else {
      return System.out;
    }
  }
}
