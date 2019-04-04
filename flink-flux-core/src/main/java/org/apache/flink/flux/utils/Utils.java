package org.apache.flink.flux.utils;

import java.io.IOException;

public final class Utils
{

  public static IOException wrapAsIOException(Exception e) {
    return new IOException(e);
  }

  private Utils() {

  }
}
