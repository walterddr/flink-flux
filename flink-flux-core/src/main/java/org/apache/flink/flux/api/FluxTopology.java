package org.apache.flink.flux.api;

import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.jobgraph.JobGraph;

import java.util.List;

/**
 * Flux topology that can be self convert into Execution job graph.
 *
 */
public interface FluxTopology {

  /**
   * Get the job graph represent the execution topology.
   */
  JobGraph getJobGraph();

  /**
   * Get extra classpath JAR required for instantiating runtime.
   */
  List<Path> getAdditionalJars();
}
