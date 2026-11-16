package dev.convertly.api.exception;

import java.util.UUID;

public class JobNotFoundException extends RuntimeException {

  public JobNotFoundException(UUID id) {
    super("Conversion job not found: " + id);
  }
}
