package dev.convertly.worker.converter;

import dev.convertly.common.Format;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

@Component
public class ImageToPdfConverter implements Converter {

  @Override
  public Set<Format> sources() {
    return Set.of(Format.PNG, Format.JPG);
  }

  @Override
  public Format target() {
    return Format.PDF;
  }

  @Override
  public Path convert(Path input, Path outDir, Map<String, String> options) throws IOException {
    if (!Files.isRegularFile(input)) {
      throw new IOException("Invalid input file: " + input);
    }

    Files.createDirectories(outDir);

    String fileName = input.getFileName().toString();
    int extensionIndex = fileName.lastIndexOf('.');
    String baseName = extensionIndex > 0 ? fileName.substring(0, extensionIndex) : fileName;

    Path output = outDir.resolve(baseName + ".pdf");

    try (PDDocument document = new PDDocument()) {
      PDImageXObject image = PDImageXObject.createFromFile(input.toString(), document);

      PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));

      document.addPage(page);

      try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
        stream.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
      }

      document.save(output.toFile());
    }

    return output;
  }
}
