package dev.convertly.worker.converter;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.convertly.common.Format;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ConverterRegistryTest {

  @Test
  void get_shouldReturnMatchingConverter() {
    Converter pngToPdf = new FakeConverter(Set.of(Format.PNG), Format.PDF);

    ConverterRegistry registry = new ConverterRegistry(List.of(pngToPdf));

    assertSame(pngToPdf, registry.get(Format.PNG, Format.PDF));
  }

  @Test
  void get_shouldThrow_whenConversionIsUnsupported() {
    Converter pngToPdf = new FakeConverter(Set.of(Format.PNG), Format.PDF);

    ConverterRegistry registry = new ConverterRegistry(List.of(pngToPdf));

    assertThrows(UnsupportedConversionException.class, () -> registry.get(Format.PDF, Format.PNG));
  }

  @Test
  void constructor_shouldThrow_whenConversionPairIsDuplicated() {
    Converter first = new FakeConverter(Set.of(Format.PNG), Format.PDF);
    Converter second = new FakeConverter(Set.of(Format.PNG), Format.PDF);

    assertThrows(IllegalStateException.class, () -> new ConverterRegistry(List.of(first, second)));
  }

  @Test
  void get_shouldReturnSameConverter_forMultipleSources() {
    Converter imageToPdf = new FakeConverter(Set.of(Format.PNG, Format.JPG), Format.PDF);

    ConverterRegistry registry = new ConverterRegistry(List.of(imageToPdf));

    assertSame(imageToPdf, registry.get(Format.PNG, Format.PDF));
    assertSame(imageToPdf, registry.get(Format.JPG, Format.PDF));
  }

  private record FakeConverter(Set<Format> sources, Format target) implements Converter {

    @Override
    public Path convert(Path input, Path outDir, Map<String, String> options) throws IOException {
      throw new UnsupportedOperationException("FakeConverter does not perform conversion");
    }
  }
}
