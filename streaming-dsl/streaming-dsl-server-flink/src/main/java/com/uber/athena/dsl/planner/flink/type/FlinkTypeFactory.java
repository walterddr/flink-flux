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

import com.uber.athena.dsl.planner.model.ArrayTypeDef;
import com.uber.athena.dsl.planner.model.MapTypeDef;
import com.uber.athena.dsl.planner.model.RowTypeDef;
import com.uber.athena.dsl.planner.model.TypeDef;
import com.uber.athena.dsl.planner.model.TypeSpecDef;
import com.uber.athena.dsl.planner.type.TypeFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.MapTypeInfo;
import org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo;
import org.apache.flink.api.java.typeutils.RowTypeInfo;

/**
 * Type factor that converts {@link TypeSpecDef} into {@link TypeInformation}.
 */
public class FlinkTypeFactory implements TypeFactory {

  @Override
  public FlinkType getType(TypeSpecDef typeSpecDef) {
    if (typeSpecDef != null) {
      return new FlinkType(constructTypeInfo(typeSpecDef));
    } else {
      return null;
    }
  }

  private static TypeInformation constructTypeInfo(TypeSpecDef typeSpecDef) {
    if (!(typeSpecDef.getTypeDef() instanceof TypeDef)) {
      throw new IllegalArgumentException("invalid type spec definition, must be of type: TypeDef");
    }
    TypeDef typeDef = (TypeDef) typeSpecDef.getTypeDef();
    if (typeDef != null) {
      return recursiveConstructTypeInformation(typeDef);
    } else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private static TypeInformation recursiveConstructTypeInformation(TypeDef typeObj) {
    if (typeObj instanceof RowTypeDef) {
      RowTypeDef rowTypeObj = (RowTypeDef) typeObj;
      return new RowTypeInfo(
          rowTypeObj
              .getFieldTypes()
              .stream()
              .map(FlinkTypeFactory::recursiveConstructTypeInformation)
              .toArray(TypeInformation[]::new),
          rowTypeObj
              .getFieldNames()
              .toArray(new String[0])
      );
    } else if (typeObj instanceof MapTypeDef) {
      MapTypeDef mapTypeObj = (MapTypeDef) typeObj;
      return new MapTypeInfo(
          recursiveConstructTypeInformation(mapTypeObj.getKeyType()),
          recursiveConstructTypeInformation(mapTypeObj.getValueType())
      );
    } else if (typeObj instanceof ArrayTypeDef) {
      ArrayTypeDef arrayTypeObj = (ArrayTypeDef) typeObj;
      return ObjectArrayTypeInfo.getInfoFor(
        recursiveConstructTypeInformation(arrayTypeObj.getElementType())
      );
    } else {
      switch (typeObj.getType()) {
        case STRING_TYPE:
          return Types.STRING;
        case BOOLEAN_TYPE:
          return Types.BOOLEAN;
        case BYTE_TYPE:
          return Types.BYTE;
        case SHORT_TYPE:
          return Types.SHORT;
        case INT_TYPE:
          return Types.INT;
        case LONG_TYPE:
          return Types.LONG;
        case FLOAT_TYPE:
          return Types.FLOAT;
        case DOUBLE_TYPE:
          return Types.DOUBLE;
        case CHAR_TYPE:
          return Types.CHAR;
        case DATE_TYPE:
          return Types.SQL_DATE;
        case VOID_TYPE:
          return Types.VOID;
        case BIG_INT_TYPE:
          return Types.BIG_INT;
        case BIG_DEC_TYPE:
          return Types.BIG_DEC;
        case INSTANT_TYPE:
          return Types.INSTANT;
        default:
          throw new IllegalArgumentException("Cannot unsupported type: " + typeObj);
      }
    }
  }
}
