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

package com.uber.athena.plugin.lib.dependencies.resolver;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static com.uber.athena.plugin.lib.dependencies.ResolverResultValidateUtils.assertArtifactResultContains;
import static org.junit.Assert.assertTrue;

/**
 * Test the depenency resolver logic.
 */
public class DependencyResolverTest {
  private static Path testDirForLocalDirOption;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    testDirForLocalDirOption = Files.createTempDirectory("dependency-resolver-test-dir");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    FileUtils.deleteQuietly(testDirForLocalDirOption.toFile());
  }

  @Test
  public void resolveDefault() throws Exception {
    DependencyResolver resolver =
        new DependencyResolver(testDirForLocalDirOption.toAbsolutePath().toString());
    Dependency dependency = new Dependency(
        new DefaultArtifact("commons-io:commons-io:2.6"), JavaScopes.COMPILE);
    resolver.setDependencies(Lists.newArrayList(dependency));
    resolver.initialize();

    List<ArtifactResult> results = resolver.resolve();

    assertTrue(results.size() > 0);
    assertArtifactResultContains(results, "commons-io", "commons-io", "2.6");
  }

  @Test
  public void resolveWithRemoteRepoList() throws Exception {
    DependencyResolver resolver = new DependencyResolver(
        testDirForLocalDirOption.toAbsolutePath().toString(),
        Collections.singletonList(
            new RemoteRepository.Builder(
                "central",
                "default",
                "https://repo1.maven.org/maven2/")
                .build()));
    Dependency dependency = new Dependency(
        new DefaultArtifact("commons-cli:commons-cli:1.4"), JavaScopes.COMPILE);
    resolver.setDependencies(Lists.newArrayList(dependency));
    resolver.initialize();

    List<ArtifactResult> results = resolver.resolve();

    assertTrue(results.size() > 0);
    assertArtifactResultContains(results, "commons-cli", "commons-cli", null);
  }

}
