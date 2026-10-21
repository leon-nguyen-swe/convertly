package dev.convertly.worker.converter;

import dev.convertly.common.Format;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Converter {
  Set<Format> sources();

  Format target();

  Path convert(Path input, Path outDir, Map<String, String> options) throws IOException;

  default Path convert(List<Path> inputs, Path outDir, Map<String, String> options)
      throws IOException {
    if (inputs.size() != 1) {
      throw new UnsupportedOperationException("This converter accepts exactly one input");
    }
    return convert(inputs.getFirst(), outDir, options);
  }
}
