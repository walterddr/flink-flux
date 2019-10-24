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

package com.uber.athena.dsl.planner.relation;

import com.uber.athena.dsl.planner.element.ElementNode;
import com.uber.athena.dsl.planner.model.VertexNode;
import com.uber.athena.dsl.planner.relation.rule.RuleExecutor;
import com.uber.athena.dsl.planner.topology.Topology;
import com.uber.athena.dsl.planner.utils.ConstructionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builder class for constructing a {@link RelationNode} object.
 */
public class RelationBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(RelationBuilder.class);

  /**
   * Configuration properties.
   */
  protected Map<String, Object> config;

  /**
   * Executor of applying rules to the topology to construct the relation.
   */
  protected RuleExecutor ruleExecutor;

  /**
   * Construct a relation builder.
   *
   * @param config configuration properties.
   * @param ruleExecutor executor of rules / ruleset.
   */
  public RelationBuilder(
      Map<String, Object> config,
      RuleExecutor ruleExecutor) {
    this.config = config;
    this.ruleExecutor = ruleExecutor;
  }

  /**
   * Construct the relation for this topology for every vertex.
   *
   * @param topology topology
   * @param elementMapping the mapping from each vertex ID to element object.
   * @return the constructed relation node for each vertex.
   */
  @SuppressWarnings("unchecked")
  public <T extends RelationNode> Map<String, T> construct(
      Topology topology,
      Map<String, ElementNode> elementMapping) throws ConstructionException {

    Map<String, RelationNode> relationMapping = new HashMap<>();

    // Construct the traverse context.
    RelationTraverseContext context = new RelationTraverseContext(topology, config);
    context.init();

    // Traverse the DAG
    while (context.hasNextVertex()) {
      String vertexId = context.visitNextVertexId();
      VertexNode vertex = topology.getVertex(vertexId);
      // invoke rule executor to construct the relation.
      LOG.debug("Traversing vertex ID" + vertexId);
      RelationNode relation = ruleExecutor.convert(
          vertex,
          elementMapping.get(vertexId),
          vertex.getUpstreams(),
          vertex
              .getUpstreams()
              .entrySet()
              .stream()
              .collect(Collectors.toMap(Map.Entry::getKey,
                  e -> relationMapping.get(e.getKey())))
          );
      relationMapping.put(vertexId, relation);
    }
    return (Map<String, T>) relationMapping;
  }
}
