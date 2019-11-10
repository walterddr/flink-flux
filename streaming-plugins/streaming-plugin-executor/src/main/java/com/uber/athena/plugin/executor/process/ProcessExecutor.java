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

import com.uber.athena.plugin.api.Executor;
import com.uber.athena.plugin.api.ExecutorPayload;
import com.uber.athena.plugin.api.PluginResult;
import com.uber.athena.plugin.base.ExceptionPluginResult;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.List;

/**
 * An impl of the {@link Executor} that uses a separated system process.
 */
@SuppressWarnings("unchecked")
public class ProcessExecutor implements Executor {

  private static final String DEFAULT_JVM_PATH = "java.home";
  private static final String DEFAULT_JVM_CLASS_PATH = "java.class.path";
  private static final String DEFAULT_JVM_LIB_PATH = "java.library.path";
  private static final ConnectedStreamType DEFAULT_IN_STREAM_TYPE = ConnectedStreamType.SOCKET;
  private static final ConnectedStreamType DEFAULT_OUT_STREAM_TYPE = ConnectedStreamType.SOCKET;

  private final String jvmVersion;
  private final String jvmClassPath;
  private final String jvmLibPath;
  private ConnectedStreamType inStreamType;
  private ConnectedStreamType outStreamType;

  public ProcessExecutor(
      ConnectedStreamType inStreamType,
      ConnectedStreamType outStreamType
  ) {
    this(DEFAULT_JVM_PATH,
        DEFAULT_JVM_CLASS_PATH,
        DEFAULT_JVM_LIB_PATH,
        inStreamType,
        outStreamType);
  }

  public ProcessExecutor() {
    this(DEFAULT_JVM_PATH,
        DEFAULT_JVM_CLASS_PATH,
        DEFAULT_JVM_LIB_PATH,
        DEFAULT_IN_STREAM_TYPE,
        DEFAULT_OUT_STREAM_TYPE);
  }

  public ProcessExecutor(
      String jvmVersion,
      String jvmClassPath,
      String jvmLibPath,
      ConnectedStreamType inStreamType,
      ConnectedStreamType outStreamType) {
    this.jvmVersion = jvmVersion;
    this.jvmClassPath = jvmClassPath;
    this.jvmLibPath = jvmLibPath;
    this.inStreamType = inStreamType;
    this.outStreamType = outStreamType;
  }

  /**
   * Starting a separate process for running the plugin.
   *
   * <p>Start a socket to get the results back. Ideally it is simpler to use
   * stdout / stderr / pipe, but many loggers will interfere.
   *
   * @return the plugin result after execution.
   * @throws IOException when separated process communication exception occurs.
   */
  @Override
  public PluginResult run(ExecutorPayload payload) throws IOException {
    ConnectedStream inStream = new ConnectedStream(inStreamType);
    ConnectedStream outStream = new ConnectedStream(outStreamType);
    try {
      inStream.open();
      outStream.open();
      List<String> args = ProcessStreamTransferUtils.buildProcessExecutorOps(
          this.jvmVersion,
          this.jvmClassPath,
          this.jvmLibPath,
          inStream.getPort(),
          outStream.getPort());
      ProcessBuilder builder = new ProcessBuilder(args);
      Process proc = builder.start();
      try (OutputStream os = new BufferedOutputStream(outStream.constructOutputStream(proc))) {
        os.write(payload.serialize());
      }
      try (ObjectInputStream is = new ObjectInputStream(inStream.constructInputStream(proc))) {
        return (PluginResult) is.readObject();
      }
    } catch (Exception e) {
      inStream.close();
      outStream.close();
      return new ExceptionPluginResult(new IOException(e));
    }
  }

  /**
   * Type of connecting stream supported.
   */
  public enum ConnectedStreamType {
    SOCKET,
    SYSTEM,
    FILE
  }

  private static class ConnectedStream implements Closeable {

    private ConnectedStreamType streamType;

    private ServerSocket serverSocket;

    ConnectedStream(ConnectedStreamType type) {
      this.streamType = type;
    }

    void open() throws IOException {
      switch (streamType) {
        case SOCKET:
          this.serverSocket = new ServerSocket();
          this.serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
          break;
        case SYSTEM:
        case FILE:
          break;
        default:
          throw new UnsupportedOperationException("Unknown stream type!" + streamType);
      }
    }

    @Override
    public void close() throws IOException {
      switch (streamType) {
        case SOCKET:
          this.serverSocket.close();
          break;
        case FILE:
        case SYSTEM:
          break;
        default:
          throw new UnsupportedOperationException("Unknown stream type!" + streamType);
      }
    }

    int getPort() {
      switch (streamType) {
        case SOCKET:
          return this.serverSocket.getLocalPort();
        case SYSTEM:
          return -1;
        case FILE:
        default:
          throw new UnsupportedOperationException("Unknown stream type!" + streamType);
      }
    }

    InputStream constructInputStream(Process proc) throws IOException {
      switch (streamType) {
        case SOCKET:
          return serverSocket.accept().getInputStream();
        case SYSTEM:
          return proc.getInputStream();
        case FILE:
        default:
          throw new UnsupportedOperationException("Unknown stream type!" + streamType);
      }
    }

    OutputStream constructOutputStream(Process proc) throws IOException {
      switch (streamType) {
        case SOCKET:
          return serverSocket.accept().getOutputStream();
        case SYSTEM:
          return proc.getOutputStream();
        case FILE:
        default:
          throw new UnsupportedOperationException("Unknown stream type!" + streamType);
      }
    }
  }
}
