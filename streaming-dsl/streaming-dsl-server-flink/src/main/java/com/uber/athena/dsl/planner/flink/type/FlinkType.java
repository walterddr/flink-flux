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

package com.uber.athena.dsl.planner.flink.type;

import com.uber.athena.dsl.planner.type.Type;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.table.api.TableSchema;

/**
 * Flink implementation of the {@link Type} with the type information.
 */
public class FlinkType implements Type {

  private TypeInformation ti;

  public FlinkType(TypeInformation ti) {
    this.ti = ti;
  }

  public FlinkType(TableSchema schema) {
    this.ti = new RowTypeInfo(schema.getFieldTypes(), schema.getFieldNames());
  }

  public TypeInformation getTypeInformation() {
    return ti;
  }
}
