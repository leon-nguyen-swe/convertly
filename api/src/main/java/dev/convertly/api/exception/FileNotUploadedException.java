package dev.convertly.api.exception;

import java.util.UUID;

public class FileNotUploadedException extends RuntimeException {
  public FileNotUploadedException(UUID id) {
    super("File not uploaded yet: " + id);
  }
}
