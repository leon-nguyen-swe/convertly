package dev.convertly.worker.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ImageToPdfConverterTest {

  private final ImageToPdfConverter converter = new ImageToPdfConverter();

  @ParameterizedTest
  @ValueSource(strings = {"sample.png", "sample.jpg", "sample.jpeg"})
  void convert_shouldCreateOnePagePdf(String fileName, @TempDir Path tempDir) throws Exception {
    URL resource = getClass().getResource("/samples/" + fileName);

    assertNotNull(resource, "Test resource not found: " + fileName);

    Path input = Path.of(resource.toURI());

    Path outputDir = tempDir.resolve("output");

    Path output = converter.convert(input, outputDir, Map.of());

    assertTrue(Files.isRegularFile(output));

    try (PDDocument pdf = Loader.loadPDF(output.toFile())) {
      assertEquals(1, pdf.getNumberOfPages());
    }
  }
}
