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

package com.uber.athena.dsl.planner.element.utils;

/**
 * Basic operator for test purpose on multiple constructor.
 */
public class BaseMultiConstructorOperator extends BaseOperator {

  private String dummyStrArg;
  private long dummyLongArg;

  public BaseMultiConstructorOperator(int num) {
    this((long) num, "");
  }

  public BaseMultiConstructorOperator(long num) {
    this(num, "");
  }

  public BaseMultiConstructorOperator(int num, String str) {
    this((long) num, str);
  }

  public BaseMultiConstructorOperator(long num, String str) {
    this.dummyLongArg = num;
    this.dummyStrArg = str;
  }

  public long getLongArg() {
    return dummyLongArg;
  }

  public String getStringArg() {
    return dummyStrArg;
  }
}