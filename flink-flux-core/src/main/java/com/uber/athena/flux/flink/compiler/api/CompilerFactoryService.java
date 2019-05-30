/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uber.athena.flux.flink.compiler.api;

import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * Unified class to search for a {@link CompilerFactory} of provided type and properties.
 */
public final class CompilerFactoryService {

  private static final ServiceLoader<CompilerFactory> DEFAULT_LOADER = ServiceLoader.load(CompilerFactory.class);
  private static final Logger LOG = LoggerFactory.getLogger(CompilerFactoryService.class);

  private CompilerFactoryService() {
    // do not instantiate
  }

  /**
   * Finds a compiler factory of the given class and descriptor.
   *
   * @param factoryClass desired factory class
   * @param <T> factory class type
   * @return the matching factory
   */
  public static <T> T find(Class<T> factoryClass) throws ClassNotFoundException {
    return findInternal(factoryClass, Collections.emptyMap(), Optional.empty());
  }

  /**
   * Finds a table factory of the given class and property map.
   *
   * @param factoryClass desired factory class
   * @param propertyMap properties that describe the factory configuration
   * @param <T> factory class type
   * @return the matching factory
   */
  public static <T> T find(
      Class<T> factoryClass,
      Map<String, String> propertyMap) throws ClassNotFoundException {
    return findInternal(factoryClass, propertyMap, Optional.empty());
  }

  /**
   * Finds a table factory of the given class, property map, and classloader.
   *
   * @param factoryClass desired factory class
   * @param propertyMap properties that describe the factory configuration
   * @param classLoader classloader for service loading
   * @param <T> factory class type
   * @return the matching factory
   */
  public static <T> T find(
      Class<T> factoryClass,
      Map<String, String> propertyMap,
      ClassLoader classLoader) throws ClassNotFoundException {
    Preconditions.checkNotNull(propertyMap);
    Preconditions.checkNotNull(classLoader);
    return findInternal(factoryClass, propertyMap, Optional.of(classLoader));
  }

  /**
   * Finds a table factory of the given class, property map, and classloader.
   *
   * @param factoryClass desired factory class
   * @param properties properties that describe the factory configuration
   * @param classLoader classloader for service loading
   * @param <T> factory class type
   * @return the matching factory
   */
  @SuppressWarnings("unchecked")
  public static <T> T findInternal(
      Class<T> factoryClass,
      Map<String, String> properties,
      Optional<ClassLoader> classLoader) throws ClassNotFoundException {

    Preconditions.checkNotNull(factoryClass);
    Preconditions.checkNotNull(properties);

    List<CompilerFactory> foundFactories = discoverFactories(classLoader);

    List<CompilerFactory> filteredFactories = filterByFactoryClass(
        factoryClass,
        properties,
        foundFactories);

    if (filteredFactories.size() > 1) {
      LOG.error(
          String.format("Multiple factory implements founded: '%s'. "
                  + "Found factories: '%s' with properties: '%s'.",
              factoryClass.getCanonicalName(), foundFactories.toString(), properties));
      throw new ClassNotFoundException(
          String.format("Multiple factory implements founded: '%s'. "
                  + "Found factories: '%s' with properties: '%s'.",
              factoryClass.getCanonicalName(), foundFactories.toString(), properties));
    }
    return (T) filteredFactories.get(0);
  }

  /**
   * Searches for factories using Java service providers.
   *
   * @return all factories in the classpath
   */
  private static List<CompilerFactory> discoverFactories(Optional<ClassLoader> classLoader) {
    try {
      List<CompilerFactory> result = new LinkedList<>();
      if (classLoader.isPresent()) {
        ServiceLoader
            .load(CompilerFactory.class, classLoader.get())
            .iterator()
            .forEachRemaining(result::add);
      } else {
        DEFAULT_LOADER.iterator().forEachRemaining(result::add);
      }
      return result;
    } catch (ServiceConfigurationError e) {
      LOG.error("Could not load service provider for compiler factories.", e);
      throw new ServiceConfigurationError("Could not load service provider for compiler factories.", e);
    }

  }

  /**
   * Filters factories with matching context by factory class.
   */
  private static <T> List<CompilerFactory> filterByFactoryClass(
      Class<T> factoryClass,
      Map<String, String> properties,
      List<CompilerFactory> foundFactories) throws ClassNotFoundException {

    List<CompilerFactory> classFactories = foundFactories.stream()
        .filter(p -> factoryClass.isAssignableFrom(p.getClass()))
        .collect(Collectors.toList());

    if (classFactories.isEmpty()) {
      LOG.error(
          String.format("No factory implements '%s'. Found factories: '%s' with properties: '%s'.",
              factoryClass.getCanonicalName(), foundFactories.toString(), properties));
      throw new ClassNotFoundException(
          String.format("No factory implements '%s'. Found factories: '%s' with properties: '%s'.",
              factoryClass.getCanonicalName(), foundFactories.toString(), properties));
    }

    return classFactories;
  }
}
