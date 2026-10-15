package dev.convertly.common;

import java.util.Locale;

public enum Format {
  PNG,
  JPG,
  PDF;

  public static Format fromFileName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("File name must not be null or blank.");
    }

    int extensionIndex = name.lastIndexOf('.');
    if (extensionIndex == -1 || extensionIndex == name.length() - 1) {
      throw new IllegalArgumentException("Invalid file name: " + name);
    }

    String extension = name.substring(extensionIndex + 1).toUpperCase(Locale.ROOT);
    try {
      return Format.valueOf(extension);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Unsupported file format: " + extension, e);
    }
  }
}
