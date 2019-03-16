package org.apache.flink.storm.flux.utils.topology;

import com.google.common.collect.Lists;
import org.apache.flink.storm.flux.api.FluxTopologyBuilder;
import org.apache.flink.storm.flux.utils.spout.RawInputFromCSVSpout;
import org.apache.flink.storm.pmml.PMMLPredictorBolt;
import org.apache.flink.storm.pmml.model.ModelOutputs;
import org.apache.flink.storm.pmml.model.jpmml.JpmmlModelOutputs;
import org.apache.flink.storm.pmml.runner.jpmml.JpmmlFactory;
import org.apache.storm.shade.org.apache.commons.io.IOUtils;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class PMMLTopologyBuilder implements FluxTopologyBuilder {

  private static final String PMML_MODEL_FILE = "KNIME_PMML_4.1_Examples_single_audit_logreg.xml";
  private static final String RAW_INPUTS_FILE = "Audit.50.csv";

  private static final String RAW_INPUT_FROM_CSV_SPOUT = "org.apache.storm.pmml.utils.rawInputFromCsvSpout";
  private static final String PMML_PREDICTOR_BOLT = "pmmLPredictorBolt";
  private static final String PRINT_BOLT_1 = "printBolt1";
  private static final String PRINT_BOLT_2 = "printBolt2";
  private static final String NON_DEFAULT_STREAM_ID = "NON_DEFAULT_STREAM_ID";

  private File rawInputs;           // Raw input data to be scored (predicted)
  private File pmml;                // PMML Model read from file - null if using Blobstore
  private String blobKey;           // PMML Model downloaded from Blobstore - null if using File
  private String tplgyName = "test";

  public PMMLTopologyBuilder() {
    this(null, null);
  }

  public PMMLTopologyBuilder(String inputFilePath, String modelFilePath) {
    rawInputs = loadFile(inputFilePath == null ? RAW_INPUTS_FILE : inputFilePath);
    pmml = loadFile(modelFilePath == null ? PMML_MODEL_FILE : modelFilePath);
  }

  private File loadFile(String example) {
    File file;
    try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(example)) {
      file = File.createTempFile("pmml-example", ".tmp");
      IOUtils.copy(stream, new FileOutputStream(file));
    } catch (IOException e) {
      throw new RuntimeException("Error loading example " + example, e);
    }
    return file;
  }

  @Override
  public TopologyBuilder getTopologyBuilder(Map<String, Object> map) {
    final TopologyBuilder builder = new TopologyBuilder();
    try {
      builder.setSpout(RAW_INPUT_FROM_CSV_SPOUT, RawInputFromCSVSpout.newInstance(rawInputs));
      builder.setBolt(PMML_PREDICTOR_BOLT, newBolt()).shuffleGrouping(RAW_INPUT_FROM_CSV_SPOUT);
      builder.setBolt(PRINT_BOLT_1, new PrinterBolt()).shuffleGrouping(PMML_PREDICTOR_BOLT);
      builder.setBolt(PRINT_BOLT_2, new PrinterBolt()).shuffleGrouping(PMML_PREDICTOR_BOLT, NON_DEFAULT_STREAM_ID);
      return builder;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private IRichBolt newBolt() throws Exception {
    final List<String> streams = Lists.newArrayList(Utils.DEFAULT_STREAM_ID, NON_DEFAULT_STREAM_ID);
    if (blobKey != null) {  // Load PMML Model from Blob store
      final ModelOutputs outFields = JpmmlModelOutputs.toStreams(blobKey, streams);
      return new PMMLPredictorBolt(new JpmmlFactory.ModelRunnerFromBlobStore(blobKey, outFields), outFields);
    } else {                // Load PMML Model from File
      final ModelOutputs outFields = JpmmlModelOutputs.toStreams(pmml, streams);
      return new PMMLPredictorBolt(new JpmmlFactory.ModelRunnerFromFile(pmml, outFields), outFields);
    }
  }

  private static class PrinterBolt extends BaseBasicBolt {
    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
      System.out.println(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
    }
  }
}
