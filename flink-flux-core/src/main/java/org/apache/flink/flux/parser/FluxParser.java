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

package org.apache.flink.flux.parser;

import org.apache.flink.flux.model.IncludeDef;
import org.apache.flink.flux.model.OperatorDef;
import org.apache.flink.flux.model.SourceDef;
import org.apache.flink.flux.model.TopologyDef;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.TypeDescription;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.constructor.Constructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Flux parser.
 */
public final class FluxParser {
  private static final Logger LOG = LoggerFactory.getLogger(FluxParser.class);
  private static final String DEFAULT_ENCODING = "UTF8";

  private FluxParser() {
  }

  public static TopologyDef parseFile(
      String inputFile,
      boolean dumpYaml,
      boolean processIncludes,
      String propertiesFile,
      boolean envSub) throws IOException {

    FileInputStream in = new FileInputStream(inputFile);
    TopologyDef topology = parseInputStream(in, dumpYaml, processIncludes, propertiesFile, envSub);
    in.close();

    return topology;
  }

  public static TopologyDef parseResource(
      String resource,
      boolean dumpYaml,
      boolean processIncludes,
      String propertiesFile,
      boolean envSub) throws IOException {

    InputStream in = FluxParser.class.getResourceAsStream(resource);
    TopologyDef topology = parseInputStream(in, dumpYaml, processIncludes, propertiesFile, envSub);
    in.close();

    return topology;
  }

  public static TopologyDef parseInputStream(
      InputStream inputStream,
      boolean dumpYaml,
      boolean processIncludes,
      String propertiesFile,
      boolean envSub) throws IOException {

    Yaml yaml = yaml();

    if (inputStream == null) {
      LOG.error("Unable to load input stream");
      System.exit(1);
    }

    TopologyDef topology = loadYaml(yaml, inputStream, propertiesFile, envSub);

    if (dumpYaml) {
      dumpYaml(topology, yaml);
    }

    if (processIncludes) {
      return processIncludes(yaml, topology, propertiesFile, envSub);
    } else {
      return topology;
    }
  }

  private static TopologyDef loadYaml(
      Yaml yaml,
      InputStream in,
      String propsFile,
      boolean envSubstitution) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    LOG.info("loading YAML from input stream...");
    int b = -1;
    while ((b = in.read()) != -1) {
      bos.write(b);
    }

    String str = bos.toString(DEFAULT_ENCODING);

    // properties file substitution
    if (propsFile != null) {
      LOG.info("Performing property substitution.");
      Properties props = new Properties();
      InputStream propsIn = new FileInputStream(propsFile);
      try {
        props.load(propsIn);
      } catch (Exception e) {
        LOG.error("Cannot load property file: " + propsFile, e);
      }
      propsIn.close();
      for (Object key : props.keySet()) {
        str = str.replace("${" + key + "}", props.getProperty((String) key));
      }
    } else {
      LOG.info("Not performing property substitution.");
    }

    // environment variable substitution
    if (envSubstitution) {
      LOG.info("Performing environment variable substitution...");
      Map<String, String> envs = System.getenv();
      for (Map.Entry<String, String> e : envs.entrySet()) {
        str = str.replace("${ENV-" + e.getKey() + "}", e.getValue());
      }
    } else {
      LOG.info("Not performing environment variable substitution.");
    }
    return (TopologyDef) yaml.load(str);
  }

  private static void dumpYaml(TopologyDef topology, Yaml yaml) {
    System.out.println("Configuration (interpreted): \n" + yaml.dump(topology));
  }

  private static Yaml yaml() {
    TypeDescription topologyDescription = new TypeDescription(TopologyDef.class);
    topologyDescription.putListPropertyType("spouts", SourceDef.class);
    topologyDescription.putListPropertyType("bolts", OperatorDef.class);
    topologyDescription.putListPropertyType("includes", IncludeDef.class);

    Constructor constructor = new Constructor(TopologyDef.class);
    constructor.addTypeDescription(topologyDescription);

    Yaml yaml = new Yaml(constructor);
    return yaml;
  }

  /**
   * @param yaml        the yaml parser for parsing the include file(s)
   * @param topologyDef the topology definition containing (possibly zero) includes
   * @return The TopologyDef with includes resolved.
   */
  private static TopologyDef processIncludes(
      Yaml yaml,
      TopologyDef topologyDef,
      String propsFile,
      boolean envSub) throws IOException {
    if (topologyDef.getIncludes() != null) {
      for (IncludeDef include : topologyDef.getIncludes()) {
        TopologyDef includeTopologyDef = null;
        if (include.isResource()) {
          LOG.info("Loading includes from resource: {}", include.getFile());
          includeTopologyDef = parseResource(include.getFile(), true, false, propsFile, envSub);
        } else {
          LOG.info("Loading includes from file: {}", include.getFile());
          includeTopologyDef = parseFile(include.getFile(), true, false, propsFile, envSub);
        }

        // if overrides are disabled, we won't replace anything that already exists
        boolean override = include.isOverride();
        // name
        if (includeTopologyDef.getName() != null) {
          topologyDef.setName(includeTopologyDef.getName(), override);
        }

        // config
        if (includeTopologyDef.getConfig() != null) {
          //TODO move this logic to the model class
          Map<String, Object> config = topologyDef.getConfig();
          Map<String, Object> includeConfig = includeTopologyDef.getConfig();
          if (override) {
            config.putAll(includeTopologyDef.getConfig());
          } else {
            for (Map.Entry<String, Object> e : includeConfig.entrySet()) {
              if (config.containsKey(e.getKey())) {
                LOG.warn("Ignoring attempt to set topology config property: "
                    + "'{}' with override == false", e.getKey());
              } else {
                config.put(e.getKey(), e.getValue());
              }
            }
          }
        }

        //component overrides
        if (includeTopologyDef.getComponents() != null) {
          topologyDef.addAllComponents(includeTopologyDef.getComponents(), override);
        }
        //bolt overrides
        if (includeTopologyDef.getOperators() != null) {
          topologyDef.addAllOperators(includeTopologyDef.getOperators(), override);
        }
        //spout overrides
        if (includeTopologyDef.getSources() != null) {
          topologyDef.addAllSources(includeTopologyDef.getSources(), override);
        }
        //stream overrides
        //TODO streams should be uniquely identifiable
        if (includeTopologyDef.getStreams() != null) {
          topologyDef.addAllStreams(includeTopologyDef.getStreams(), override);
        }
      } // end include processing
    }
    return topologyDef;
  }
}
