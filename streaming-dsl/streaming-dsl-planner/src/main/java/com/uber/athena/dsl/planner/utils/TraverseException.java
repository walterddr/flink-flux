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

package com.uber.athena.dsl.planner.utils;

/**
 * Exception for parsing {@code TopologyDef}.
 */
public class TraverseException extends ConstructionException {

  /**
   * Constructs an {@code TraverseException} with {@code null}
   * as its error detail message.
   */
  public TraverseException() {
    super();
  }

  /**
   * Constructs an {@code TraverseException} with the specified detail message.
   *
   * @param message
   *        The detail message (which is saved for later retrieval
   *        by the {@link #getMessage()} method)
   */
  public TraverseException(String message) {
    super(message);
  }

  /**
   * Constructs an {@code TraverseException} with the specified detail message
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
  public TraverseException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs an {@code TraverseException} with the specified cause.
   * also with a detail message of {@code (cause==null ? null : cause.toString())}
   *
   * @param cause
   *        The cause (which is saved for later retrieval by the
   *        {@link #getCause()} method).  (A null value is permitted,
   *        and indicates that the cause is nonexistent or unknown.)
   *
   * @since 1.6
   */
  public TraverseException(Throwable cause) {
    super(cause);
  }
}
