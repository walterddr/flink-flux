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

package com.uber.athena.flux.converter.impl.testutils.node.element;

import com.uber.athena.flux.converter.api.node.Node;
import com.uber.athena.flux.converter.api.node.element.ElementNode;
import com.uber.athena.flux.converter.impl.testutils.operator.Operator;
import com.uber.athena.flux.model.VertexDef;

public abstract class BaseElementNode implements ElementNode {

  protected Operator constructedElement;
  protected String vertexId;
  protected VertexDef vertexDef;
  protected String digest;

  public BaseElementNode(String vertexId, VertexDef vertexDef) {
    this.vertexId = vertexId;
    this.vertexDef = vertexDef;
  }

  @Override
  public String getVertexId() {
    return vertexId;
  }

  @Override
  public VertexDef getVertexDef() {
    return vertexDef;
  }

  @Override
  public String getDigest() {
    return digest;
  }

  @Override
  public void computeDigest() {
    this.digest = computeElementDigest(this.getClass(), this.vertexId, this.constructedElement);
  }

  @Override
  public Object getElement() {
    return constructedElement;
  }

  public void setElement(Operator constructedElement) {
    this.constructedElement = constructedElement;
  }

  protected static String computeElementDigest(
      Class<? extends Node> nodeClazz,
      String vertexId,
      Operator element) {
    return nodeClazz.getSimpleName() + "(" + vertexId + "):\n" + element.getDescription();
  }
}
