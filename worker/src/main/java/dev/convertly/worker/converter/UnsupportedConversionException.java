package dev.convertly.worker.converter;

import dev.convertly.common.Format;

public class UnsupportedConversionException extends RuntimeException {

  public UnsupportedConversionException(Format from, Format to) {
    super("Unsupported conversion: " + from + " -> " + to);
  }
}
