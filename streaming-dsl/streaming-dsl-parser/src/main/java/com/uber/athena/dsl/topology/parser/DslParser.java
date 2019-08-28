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

package com.uber.athena.dsl.topology.parser;

import com.uber.athena.dsl.model.TopologyDef;
import com.uber.athena.dsl.topology.exceptions.ParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Parse a {@link TopologyDef} from its stringify, user-friendly format.
 */
public final class DslParser {
  private static final Logger LOG = LoggerFactory.getLogger(DslParser.class);
  private static final String DEFAULT_ENCODING = "UTF8";

  private DslParser() {
  }

  public static TopologyDef parseFile(
      String inputFile,
      boolean dumpYaml,
      String propertiesFile,
      boolean envSub) throws IOException, ParsingException {

    FileInputStream in = new FileInputStream(inputFile);
    TopologyDef topology = parseInputStream(in, dumpYaml, propertiesFile, envSub);
    in.close();

    return topology;
  }

  public static TopologyDef parseResource(
      String resource,
      boolean dumpYaml,
      String propertiesFile,
      boolean envSub) throws IOException, ParsingException {

    InputStream in = DslParser.class.getResourceAsStream(resource);
    TopologyDef topology = parseInputStream(in, dumpYaml, propertiesFile, envSub);
    in.close();

    return topology;
  }

  public static TopologyDef parseInputStream(
      InputStream inputStream,
      boolean dumpYaml,
      String propertiesFile,
      boolean envSub) throws ParsingException {

    Yaml yaml = yaml();

    if (inputStream == null) {
      LOG.error("Unable to load input stream");
      System.exit(1);
    }

    TopologyDef topology = loadYaml(yaml, inputStream, propertiesFile, envSub);

    if (dumpYaml) {
      dumpYaml(topology, yaml);
    }
    return topology;
  }

  private static TopologyDef loadYaml(
      Yaml yaml,
      InputStream in,
      String propsFile,
      boolean envSubstitution) throws ParsingException {
    try {
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
    } catch (IOException e) {
      throw new ParsingException("Unable to parse YAML", e);
    }
  }

  private static void dumpYaml(TopologyDef topology, Yaml yaml) {
    System.out.println("Configuration (interpreted): \n" + yaml.dump(topology));
  }

  private static Yaml yaml() {
    TypeDescription topologyDescription = new TypeDescription(TopologyDef.class);

    Constructor constructor = new Constructor(TopologyDef.class);
    constructor.addTypeDescription(topologyDescription);

    Yaml yaml = new Yaml(constructor);
    return yaml;
  }
}
