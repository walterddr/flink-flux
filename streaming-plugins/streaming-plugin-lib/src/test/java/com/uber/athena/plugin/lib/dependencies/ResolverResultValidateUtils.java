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
 *
 */

package com.uber.athena.plugin.lib.dependencies;

import org.eclipse.aether.resolution.ArtifactResult;

import java.util.Collection;
import java.util.Map;

/**
 * Utilities to validate dependency resolved results.
 */
public final class ResolverResultValidateUtils {

  private ResolverResultValidateUtils() {

  }

  public static void assertArtifactResultContains(
      Collection<ArtifactResult> results,
      String groupId,
      String artifactId,
      String version) {
    for (ArtifactResult result : results) {
      if (result.isResolved()
          && result.getArtifact().getGroupId().equals(groupId)
          && result.getArtifact().getArtifactId().equals(artifactId)) {
        if (version == null || result.getArtifact().getVersion().equals(version)) {
          return;
        }
      }
    }
    throw new AssertionError("Result doesn't contain expected artifact!"
        + " expecting: [" + groupId + ":" + artifactId + ":" + version + "]");
  }

  public static void assertArtifactPathMapContains(
      Map<String, String> artifactToPathMapping,
      String groupId,
      String artifactId) {
    for (Map.Entry<String, String> pathEntry : artifactToPathMapping.entrySet()) {
      if (pathEntry.getKey().startsWith(groupId + ":" + artifactId + ":")) {
        return;
      }
    }
    throw new AssertionError("Result doesn't contain expected artifact!"
        + " expecting: [" + groupId + ":" + artifactId + "]");
  }
}
