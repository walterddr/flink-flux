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

package com.uber.athena.dsl.planner.flink.test.utils.datastream.type;

import com.uber.athena.dsl.planner.model.ArrayTypeDef;
import com.uber.athena.dsl.planner.model.MapTypeDef;
import com.uber.athena.dsl.planner.model.RowTypeDef;
import com.uber.athena.dsl.planner.model.TypeDef;
import com.uber.athena.dsl.planner.model.TypeSpecDef;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Short.parseShort;

/**
 * Test source for type generics and type factories.
 * @param <T> the type this generic source produces.
 */
public class GenericSource<T> implements SourceFunction<T> {

  private static final Random RAND = new Random();

  private final int repeat;
  private final long intervalMs;

  private TypeSpecDef typeSpecDef;

  public GenericSource(int repeat) {
    this(repeat, 10);
  }

  public GenericSource(int repeat, long intervalMs) {
    this.repeat = repeat;
    this.intervalMs = intervalMs;
  }

  @Override
  public void run(SourceContext<T> sourceContext) throws Exception {
    for (int i = 0; i < repeat; i++) {
      sourceContext.collect(genData((TypeDef) typeSpecDef.getTypeDef()));
      Thread.sleep(intervalMs);
    }
  }

  @Override
  public void cancel() {
  }

  @SuppressWarnings("unchecked")
  private static <T> T genData(TypeDef typeObj) {
    if (typeObj instanceof RowTypeDef) {
      RowTypeDef rowTypeObj = (RowTypeDef) typeObj;
      Row row = new Row(rowTypeObj.getFieldNames().size());
      int idx = 0;
      for (TypeDef rowElementType : rowTypeObj.getFieldTypes()) {
        row.setField(idx, genData(rowElementType));
        idx++;
      }
      return (T) row;
    } else {
      switch (typeObj.getType()) {
        case STRING_TYPE:
          return randomlySelect("foo", "bar");
        case BOOLEAN_TYPE:
          return randomlySelect(true, false);
        case BYTE_TYPE:
          return randomlySelect("foo".getBytes(), "bar".getBytes());
        case SHORT_TYPE:
          return randomlySelect(parseShort("1"), parseShort("2"));
        case INT_TYPE:
          return randomlySelect(parseInt("1"), parseInt("2"));
        case LONG_TYPE:
          return randomlySelect(parseLong("1"), parseLong("2"));
        case FLOAT_TYPE:
          return randomlySelect(parseFloat("1.1"), parseFloat("2.2"));
        case DOUBLE_TYPE:
          return randomlySelect(parseDouble("1.1"), parseDouble("2.2"));
        case CHAR_TYPE:
          return randomlySelect('a', 'b');
        case ARRAY_TYPE:
          List<Object> arr = new ArrayList<>();
          arr.add(genData(((ArrayTypeDef) typeObj).getElementType()));
          return (T) arr.toArray();
        case MAP_TYPE:
          Map map = new HashMap<>();
          map.put(
              genData(((MapTypeDef) typeObj).getKeyType()),
              genData(((MapTypeDef) typeObj).getValueType()));
          return (T) map;
        default:
          throw new IllegalArgumentException(
              "Cannot generate random data for type: " + typeObj.getType());
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> T randomlySelect(Object... obj) {
    return (T) obj[RAND.nextInt(obj.length)];
  }

  public TypeSpecDef getTypeSpecDef() {
    return typeSpecDef;
  }

  public void setTypeSpecDef(TypeSpecDef typeSpecDef) {
    this.typeSpecDef = typeSpecDef;
  }
}
