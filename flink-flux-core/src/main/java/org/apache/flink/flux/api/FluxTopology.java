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
   * @return job graph
   */
  JobGraph getJobGraph();

  /**
   * Get extra classpath JAR required for instantiating runtime.
   * @return list of paths that indicates JAR files needed in the classpath
   */
  List<Path> getAdditionalJars();
}
