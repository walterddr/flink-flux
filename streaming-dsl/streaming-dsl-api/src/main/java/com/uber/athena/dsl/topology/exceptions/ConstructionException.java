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

package com.uber.athena.dsl.topology.exceptions;

/**
 * Exception for constructing {@code Topology}.
 */
public class ConstructionException extends Exception {

  /**
   * Constructs an {@code ConstructionException} with {@code null}
   * as its error detail message.
   */
  public ConstructionException() {
    super();
  }

  /**
   * Constructs an {@code ConstructionException} with the specified detail message.
   *
   * @param message
   *        The detail message (which is saved for later retrieval
   *        by the {@link #getMessage()} method)
   */
  public ConstructionException(String message) {
    super(message);
  }

  /**
   * Constructs an {@code ConstructionException} with the specified detail message
   * and cause.
   *
   * @param message
   *        The detail message (which is saved for later retrieval
   *        by the {@link #getMessage()} method)
   *
   * @param cause
   *        The cause (which is saved for later retrieval by the
   *        {@link #getCause()} method).  (A null value is permitted,
   *        and indicates that the cause is nonexistent or unknown.)
   *
   * @since 1.6
   */
  public ConstructionException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs an {@code ConstructionException} with the specified cause.
   * also with a detail message of {@code (cause==null ? null : cause.toString())}
   *
   * @param cause
   *        The cause (which is saved for later retrieval by the
   *        {@link #getCause()} method).  (A null value is permitted,
   *        and indicates that the cause is nonexistent or unknown.)
   *
   * @since 1.6
   */
  public ConstructionException(Throwable cause) {
    super(cause);
  }
}