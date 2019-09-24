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

package com.uber.athena.dsl.planner.element.constructor;

import com.uber.athena.dsl.planner.model.ConfigMethodDef;
import com.uber.athena.dsl.planner.model.ObjectDef;
import com.uber.athena.dsl.planner.model.PropertyDef;
import com.uber.athena.dsl.planner.topology.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Basic element construction utility using Reflection.
 *
 * <p>The util tries to locate the Clazz from its class name and constructor
 * argument combination.
 */
@SuppressWarnings("unchecked")
public final class ReflectiveConstructUtils {
  private static final Logger LOG = LoggerFactory.getLogger(ReflectiveConstructUtils.class);

  private ReflectiveConstructUtils() {
  }

  public static Object buildObject(
      ObjectDef def,
      Topology topology) throws ReflectiveOperationException {
    Class clazz = Class.forName(def.getClassName());
    Object obj = null;
    if (def.getConstructorArgs() != null && def.getConstructorArgs().size() > 0) {
      LOG.debug("Found constructor arguments in definition: "
          + def.getConstructorArgs().getClass().getName());
      List<Object> cArgs = def.getConstructorArgs();

      if (def.getHasReferenceInArgs()) {
        cArgs = ComponentResolutionUtils.resolveReferences(cArgs, topology);
      }

      Constructor con = ReflectiveConstructUtils.findCompatibleConstructor(cArgs, clazz);
      if (con != null) {
        LOG.debug("Found something seemingly compatible, attempting invocation...");
        obj = con.newInstance(
            ReflectiveConstructUtils.getArgsWithListCoercian(cArgs, con.getParameterTypes()));
      } else {
        String msg = String.format(
            "Couldn't find a suitable constructor for class '%s' with arguments '%s'.",
            clazz.getName(),
            cArgs);
        throw new IllegalArgumentException(msg);
      }
    } else {
      obj = clazz.getConstructor().newInstance();
    }
    applyProperties(def, obj, topology);
    invokeConfigMethods(def, obj, topology);
    return obj;
  }

  private static void applyProperties(
      ObjectDef bean,
      Object instance,
      Topology topology) throws ReflectiveOperationException {
    List<PropertyDef> props = bean.getPropertyList();
    Class clazz = instance.getClass();
    if (props != null) {
      Map<String, Object> propertyToObjectMap =
          ComponentResolutionUtils.resolveProperties(props, topology);
      for (Map.Entry<String, Object> e: propertyToObjectMap.entrySet()) {
        Method setter = findSetter(clazz, e.getKey(), e.getValue());
        if (setter != null) {
          LOG.debug("found setter, attempting to invoke");
          // invoke setter
          setter.invoke(instance, e.getValue());
        } else {
          // look for a public instance variable
          LOG.debug("no setter found. Looking for a public instance variable...");
          Field field = findPublicField(clazz, e.getKey(), e.getValue());
          if (field != null) {
            field.set(instance, e.getValue());
          }
        }
      }
    }
  }

  private static void invokeConfigMethods(
      ObjectDef bean,
      Object instance,
      Topology topology) throws InvocationTargetException, IllegalAccessException {

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
        args = ComponentResolutionUtils.resolveReferences(args, topology);
      }
      String methodName = methodDef.getName();
      Method method = ReflectiveConstructUtils.findCompatibleMethod(args, clazz, methodName);
      if (method != null) {
        Object[] methodArgs =
            ReflectiveConstructUtils.getArgsWithListCoercian(args, method.getParameterTypes());
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
  // Field setter and getter utilities
  // ------------------------------------------------------------------------

  private static Field findPublicField(
      Class clazz,
      String property,
      Object arg) throws NoSuchFieldException {
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


  /**
   * Given a list of constructor arguments and target class, attempt to find a suitable constructor.
   *
   * @param args   argument list
   * @param target target classs
   * @return constructor method
   * @throws NoSuchMethodException cannot be found
   */
  public static Constructor findCompatibleConstructor(List<Object> args, Class target)
      throws NoSuchMethodException {
    Constructor retval = null;
    int eligibleCount = 0;

    LOG.debug("Target class: {}", target.getName());
    Constructor[] cons = target.getDeclaredConstructors();

    for (Constructor con : cons) {
      Class[] paramClasses = con.getParameterTypes();
      if (paramClasses.length == args.size()) {
        LOG.debug("found constructor with same number of args..");
        boolean invokable = canInvokeWithArgs(args, con.getParameterTypes());
        if (invokable) {
          retval = con;
          eligibleCount++;
        }
        LOG.debug("** invokable --> {}", invokable);
      } else {
        LOG.debug("Skipping constructor with wrong number of arguments.");
      }
    }
    if (eligibleCount > 1) {
      LOG.warn("Found multiple invokable constructors "
              + "for class {}, given arguments {}. Using the last one found.", target, args);
    }
    return retval;
  }

  /**
   * Find compatible methods for a specific list of arguments and a class reference.
   *
   * @param args       arguments
   * @param target     target class
   * @param methodName method name
   * @return java.lang.Method
   */
  public static Method findCompatibleMethod(List<Object> args, Class target, String methodName) {
    Method retval = null;
    int eligibleCount = 0;

    LOG.debug("Target class: {}", target.getName());
    Method[] methods = target.getMethods();

    for (Method method : methods) {
      Class[] paramClasses = method.getParameterTypes();
      if (paramClasses.length == args.size() && method.getName().equals(methodName)) {
        LOG.debug("found constructor with same number of args..");
        boolean invokable = false;
        if (args.size() == 0) {
          // it's a method with zero args
          invokable = true;
        } else {
          invokable = canInvokeWithArgs(args, method.getParameterTypes());
        }
        if (invokable) {
          retval = method;
          eligibleCount++;
        }
        LOG.debug("** invokable --> {}", invokable);
      } else {
        LOG.debug("Skipping method with wrong number of arguments.");
      }
    }
    if (eligibleCount > 1) {
      LOG.warn("Found multiple invokable methods for class {}, method {}, given arguments {}. "
              + "Using the last one found.", new Object[]{target, methodName, args});
    }
    return retval;
  }

  /**
   * Return argument with list coercian.
   *
   * <p>Given a java.util.List of contructor/method arguments, and a list of
   * parameter types, attempt to convert the list to an java.lang.Object array
   * that can be used to invoke the constructor. If an argument needs to be
   * coerced from a List to an Array, do so.
   *
   * @param args           list of arguments
   * @param parameterTypes list of parameter types
   * @return argument object list.
   */
  public static Object[] getArgsWithListCoercian(List<Object> args, Class[] parameterTypes) {
    if (parameterTypes.length != args.size()) {
      throw new IllegalArgumentException(
          "Contructor parameter count does not egual argument size.");
    }
    Object[] constructorParams = new Object[args.size()];

    // loop through the arguments, if we hit a list that has to be convered to an array,
    // perform the conversion
    for (int i = 0; i < args.size(); i++) {
      Object obj = args.get(i);
      Class paramType = parameterTypes[i];
      Class objectType = obj.getClass();
      LOG.debug("Comparing parameter class {} to object class {} to see if assignment is possible.",
          paramType, objectType);
      if (paramType.equals(objectType)) {
        LOG.debug("They are the same class.");
        constructorParams[i] = args.get(i);
        continue;
      }
      if (paramType.isAssignableFrom(objectType)) {
        LOG.debug("Assignment is possible.");
        constructorParams[i] = args.get(i);
        continue;
      }
      if (isPrimitiveBoolean(paramType) && Boolean.class.isAssignableFrom(objectType)) {
        LOG.debug("Its a primitive boolean.");
        Boolean bool = (Boolean) args.get(i);
        constructorParams[i] = bool;
        continue;
      }
      if (isPrimitiveNumber(paramType) && Number.class.isAssignableFrom(objectType)) {
        LOG.debug("Its a primitive number.");
        Number num = (Number) args.get(i);
        if (paramType == Float.TYPE) {
          constructorParams[i] = num.floatValue();
        } else if (paramType == Double.TYPE) {
          constructorParams[i] = num.doubleValue();
        } else if (paramType == Long.TYPE) {
          constructorParams[i] = num.longValue();
        } else if (paramType == Integer.TYPE) {
          constructorParams[i] = num.intValue();
        } else if (paramType == Short.TYPE) {
          constructorParams[i] = num.shortValue();
        } else if (paramType == Byte.TYPE) {
          constructorParams[i] = num.byteValue();
        } else {
          constructorParams[i] = args.get(i);
        }
        continue;
      }

      // enum conversion
      if (paramType.isEnum() && objectType.equals(String.class)) {
        LOG.debug("Yes, will convert a String to enum");
        constructorParams[i] = Enum.valueOf(paramType, (String) args.get(i));
        continue;
      }

      // List to array conversion
      if (paramType.isArray() && List.class.isAssignableFrom(objectType)) {
        // TODO more collection content type checking
        LOG.debug("Conversion appears possible...");
        List list = (List) obj;
        LOG.debug("Array Type: {}, List type: {}",
            paramType.getComponentType(), list.get(0).getClass());

        // create an array of the right type
        Object newArrayObj = Array.newInstance(paramType.getComponentType(), list.size());
        for (int j = 0; j < list.size(); j++) {
          Array.set(newArrayObj, j, list.get(j));

        }
        constructorParams[i] = newArrayObj;
        LOG.debug("After conversion: {}", constructorParams[i]);
      }
    }
    return constructorParams;
  }

  /**
   * Determine if argument can be invoked by constructor.
   *
   * <p>If the given constructor/method parameter types are compatible given
   * arguments List. Consider if list coercian can make it possible.
   *
   * @param args           arguments
   * @param parameterTypes parameter types for setting
   * @return whether can be invoked from
   */
  private static boolean canInvokeWithArgs(List<Object> args, Class[] parameterTypes) {
    if (parameterTypes.length != args.size()) {
      LOG.warn("parameter types were the wrong size");
      return false;
    }

    for (int i = 0; i < args.size(); i++) {
      Object obj = args.get(i);
      Class paramType = parameterTypes[i];
      Class objectType = obj.getClass();
      LOG.debug("Comparing parameter class {} to object class {} to see if assignment is possible.",
          paramType, objectType);
      if (paramType.equals(objectType)) {
        LOG.debug("Yes, they are the same class.");
      } else if (paramType.isAssignableFrom(objectType)) {
        LOG.debug("Yes, assignment is possible.");
      } else if (isPrimitiveBoolean(paramType) && Boolean.class.isAssignableFrom(objectType)) {
        LOG.debug("Yes, assignment is possible.");
      } else if (isPrimitiveNumber(paramType) && Number.class.isAssignableFrom(objectType)) {
        LOG.debug("Yes, assignment is possible.");
      } else if (paramType.isEnum() && objectType.equals(String.class)) {
        LOG.debug("Yes, will convert a String to enum");
      } else if (paramType.isArray() && List.class.isAssignableFrom(objectType)) {
        // TODO more collection content type checking
        LOG.debug("Assignment is possible if we convert a List to an array.");
        LOG.debug("Array Type: {}, List type: {}",
            paramType.getComponentType(), ((List) obj).get(0).getClass());
      } else {
        return false;
      }
    }
    return true;
  }

  private static boolean isPrimitiveNumber(Class clazz) {
    return clazz.isPrimitive() && !clazz.equals(boolean.class);
  }

  private static boolean isPrimitiveBoolean(Class clazz) {
    return clazz.isPrimitive() && clazz.equals(boolean.class);
  }
}
