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

package com.uber.athena.flux.flink.compiler.impl.datastream.utils;

import com.uber.athena.flux.flink.compiler.context.CompilerContext;
import com.uber.athena.flux.flink.compiler.context.CompilerVertex;
import com.uber.athena.flux.flink.compiler.impl.datastream.DataStreamCompilerVertex;
import com.uber.athena.flux.flink.compiler.utils.ReflectiveInvokeUtils;
import com.uber.athena.flux.model.ConfigMethodDef;
import com.uber.athena.flux.model.ObjectDef;
import com.uber.athena.flux.model.OperatorDef;
import com.uber.athena.flux.model.PropertyDef;
import com.uber.athena.flux.model.SinkDef;
import com.uber.athena.flux.model.SourceDef;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.operators.OneInputStreamOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public final class DataStreamCompilationUtils {
  private static final Logger LOG = LoggerFactory.getLogger(DataStreamCompilationUtils.class);

  private DataStreamCompilationUtils() {

  }

  /**
   * compile source.
   *
   * @param compilerContext flux context
   * @param senv        stream execution environment to start the source definition
   * @param vertex      compilation vertex
   * @throws Exception when compilation fails.
   */
  public static void compileSource(
      CompilerContext compilerContext,
      StreamExecutionEnvironment senv,
      DataStreamCompilerVertex vertex) throws Exception {
    // Compile vertex
    SourceDef sourceDef = (SourceDef) vertex.getVertex();
    SourceFunction sourceFunction = (SourceFunction) buildObject(sourceDef, compilerContext);
    DataStreamSource dataStreamSource = senv.addSource(sourceFunction, sourceDef.getId());

    // set compilation results
    vertex.setCompilationResult(dataStreamSource);
    compilerContext.addSource(sourceDef.getId(), vertex);
  }

  /**
   * compile operator.
   *
   * @param compilerContext flux context
   * @param vertex      compilation vertex
   * @throws Exception when compilation fails.
   */
  public static void compileOperator(
      CompilerContext compilerContext,
      DataStreamCompilerVertex vertex) throws Exception {
    if (vertex.getIncomingEdge().size() != 1) {
      throw new UnsupportedOperationException(
          "Cannot compile zero input or multiple input operators as this moment");
    }
    // Fetch upstream
    OperatorDef operatorDef = (OperatorDef) vertex.getVertex();
    String sourceId = vertex.getIncomingEdge().get(0).getFromVertex();
    CompilerVertex source = compilerContext.getCompilationVertex(sourceId);
    DataStream sourceStream = ((DataStreamCompilerVertex) source).getCompilationResult();

    // Compile vertex
    // TODO: @walterddr get this part flexibly converted based on input/output edge type
    OneInputStreamOperator operator = (OneInputStreamOperator) buildObject(operatorDef, compilerContext);
    DataStream stream = sourceStream.transform(
        operatorDef.getId(),
        BasicTypeInfo.STRING_TYPE_INFO,
        operator);

    // set compilation results
    vertex.setCompilationResult(stream);
    compilerContext.addOperator(operatorDef.getId(), vertex);
  }

  /**
   * compile sink.
   *
   * @param compilerContext flux context
   * @param vertex      compilation vertex
   * @throws Exception when compilation fails.
   */
  public static void compileSink(
      CompilerContext compilerContext,
      DataStreamCompilerVertex vertex) throws Exception {
    if (vertex.getIncomingEdge().size() != 1) {
      throw new UnsupportedOperationException(
          "Cannot compile zero input or multiple input sink as this moment");
    }
    // Fetch upstream
    SinkDef sinkDef = (SinkDef) vertex.getVertex();
    String sourceId = vertex.getIncomingEdge().get(0).getFromVertex();
    CompilerVertex source = compilerContext.getCompilationVertex(sourceId);
    DataStream sourceStream = ((DataStreamCompilerVertex) source).getCompilationResult();

    // Compile vertex
    // TODO: @walterddr get this part flexibly converted based on input/output edge type
    SinkFunction sink = (SinkFunction) buildObject(sinkDef, compilerContext);
    // returned DataStreamSink is ignored
    sourceStream.addSink(sink);

    // set compilation results
    compilerContext.addSink(sinkDef.getId(), vertex);
  }

  private static Object buildObject(ObjectDef def, CompilerContext compilerContext) throws Exception {
    Class clazz = Class.forName(def.getClassName());
    Object obj = null;
    if (def.getConstructorArgs() != null && def.getConstructorArgs().size() > 0) {
      LOG.debug("Found constructor arguments in definition: " + def.getConstructorArgs().getClass().getName());
      List<Object> cArgs = def.getConstructorArgs();

      if (def.getHasReferenceInArgs()) {
        cArgs = ReflectiveInvokeUtils.resolveReferences(cArgs, compilerContext);
      }

      Constructor con = ReflectiveInvokeUtils.findCompatibleConstructor(cArgs, clazz);
      if (con != null) {
        LOG.debug("Found something seemingly compatible, attempting invocation...");
        obj = con.newInstance(
            ReflectiveInvokeUtils.getArgsWithListCoercian(cArgs, con.getParameterTypes()));
      } else {
        String msg = String.format(
            "Couldn't find a suitable constructor for class '%s' with arguments '%s'.",
            clazz.getName(),
            cArgs);
        throw new IllegalArgumentException(msg);
      }
    } else {
      obj = clazz.newInstance();
    }
    applyProperties(def, obj, compilerContext);
    invokeConfigMethods(def, obj, compilerContext);
    return obj;
  }

  private static void applyProperties(ObjectDef bean, Object instance, CompilerContext context)
      throws Exception {
    List<PropertyDef> props = bean.getPropertyList();
    Class clazz = instance.getClass();
    if (props != null) {
      for (PropertyDef prop : props) {
        Object value = prop.getReference() != null
            ? context.getComponent(prop.getReference()) : prop.getValue();
        Method setter = findSetter(clazz, prop.getName(), value);
        if (setter != null) {
          LOG.debug("found setter, attempting to invoke");
          // invoke setter
          setter.invoke(instance, new Object[]{value});
        } else {
          // look for a public instance variable
          LOG.debug("no setter found. Looking for a public instance variable...");
          Field field = findPublicField(clazz, prop.getName(), value);
          if (field != null) {
            field.set(instance, value);
          }
        }
      }
    }
  }

  private static void invokeConfigMethods(ObjectDef bean, Object instance, CompilerContext context)
      throws InvocationTargetException, IllegalAccessException {

    List<ConfigMethodDef> methodDefs = bean.getConfigMethods();
    if (methodDefs == null || methodDefs.size() == 0) {
      return;
    }
    Class clazz = instance.getClass();
    for (ConfigMethodDef methodDef : methodDefs) {
      List<Object> args = methodDef.getConfigArgs();
      if (args == null) {
        args = new ArrayList<>();
      }
      if (methodDef.getHasReferenceInArgs()) {
        args = ReflectiveInvokeUtils.resolveReferences(args, context);
      }
      String methodName = methodDef.getName();
      Method method = ReflectiveInvokeUtils.findCompatibleMethod(args, clazz, methodName);
      if (method != null) {
        Object[] methodArgs =
            ReflectiveInvokeUtils.getArgsWithListCoercian(args, method.getParameterTypes());
        method.invoke(instance, methodArgs);
      } else {
        String msg = String.format(
            "Unable to find configuration method '%s' in class '%s' with arguments %s.",
            methodName, clazz.getName(), args);
        throw new IllegalArgumentException(msg);
      }
    }
  }

  // ------------------------------------------------------------------------
  // Type utilities
  // ------------------------------------------------------------------------

  private static TypeInformation resolveTypeInformation(String typeInformation) {
    switch (typeInformation.toLowerCase()) {
      case "string":
        return BasicTypeInfo.STRING_TYPE_INFO;
      case "boolean":
        return BasicTypeInfo.BOOLEAN_TYPE_INFO;
      case "byte":
        return BasicTypeInfo.BYTE_TYPE_INFO;
      case "short":
        return BasicTypeInfo.SHORT_TYPE_INFO;
      case "int":
        return BasicTypeInfo.INT_TYPE_INFO;
      case "long":
        return BasicTypeInfo.LONG_TYPE_INFO;
      case "float":
        return BasicTypeInfo.FLOAT_TYPE_INFO;
      case "double":
        return BasicTypeInfo.DOUBLE_TYPE_INFO;
      case "char":
        return BasicTypeInfo.CHAR_TYPE_INFO;
      case "date":
        return BasicTypeInfo.DATE_TYPE_INFO;
      case "void":
        return BasicTypeInfo.VOID_TYPE_INFO;
      default:
        throw new IllegalArgumentException("operator type info is not supported: " + typeInformation);
    }
  }

  // ------------------------------------------------------------------------
  // Field setter and getter utilities
  // ------------------------------------------------------------------------

  private static Field findPublicField(Class clazz, String property, Object arg) throws NoSuchFieldException {
    Field field = clazz.getField(property);
    return field;
  }

  private static Method findSetter(Class clazz, String property, Object arg) {
    String setterName = toSetterName(property);
    Method retval = null;
    Method[] methods = clazz.getMethods();
    for (Method method : methods) {
      if (setterName.equals(method.getName())) {
        LOG.debug("Found setter method: " + method.getName());
        retval = method;
      }
    }
    return retval;
  }

  private static String toSetterName(String name) {
    return "set" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
  }

}
