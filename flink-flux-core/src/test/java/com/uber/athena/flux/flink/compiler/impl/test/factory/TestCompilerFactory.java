package com.uber.athena.flux.flink.compiler.impl.test.factory;

import com.uber.athena.flux.flink.compiler.api.Compiler;
import com.uber.athena.flux.flink.compiler.api.CompilerFactory;
import com.uber.athena.flux.flink.compiler.impl.test.TestCompilerImpl;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

public class TestCompilerFactory implements CompilerFactory {

  private static final Compiler<DataStream> COMPILER = new TestCompilerImpl();

  @Override
  public Compiler<?> getCompiler(Class<?> ObjectClass, Map<String, String> properties) {
    return COMPILER;
  }

  @Override
  public Compiler<?> getCompiler(String className, Map<String, String> properties) {
    return COMPILER;
  }
}
