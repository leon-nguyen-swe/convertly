package dev.convertly.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

public class FormatTest {

  @ParameterizedTest
  @EnumSource(Format.class)
  void fromFileName_shouldReturnFormat_whenExtensionIsSupported(Format expectedFormat) {

    String fileName = "image." + expectedFormat.name().toLowerCase();

    Format actualFormat = Format.fromFileName(fileName);

    assertEquals(expectedFormat, actualFormat);
  }

  @ParameterizedTest
  @CsvSource({
    "scan.PNG, PNG",
    "image.png, PNG",
    "image.PnG, PNG",
    "document.pdf, PDF",
    "photo.jpg, JPG",
    "photo.jpeg, JPG"
  })
  void fromFileName_shouldSupportDifferentExtensionCases(String fileName, Format expectedFormat) {

    Format actualFormat = Format.fromFileName(fileName);

    assertEquals(expectedFormat, actualFormat);
  }

  @Test
  void fromFileName_shouldThrowException_whenNameIsNull() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Format.fromFileName(null));

    assertEquals("File name must not be null or blank.", exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
  void fromFileName_shouldThrowException_whenNameIsBlank(String name) {
    assertThrows(IllegalArgumentException.class, () -> Format.fromFileName(name));
  }

  @ParameterizedTest
  @ValueSource(strings = {"image", "image.", ".", ""})
  void fromFileName_shouldThrowException_whenExtensionIsMissing(String name) {
    assertThrows(IllegalArgumentException.class, () -> Format.fromFileName(name));
  }

  @Test
  void fromFileName_shouldThrowException_whenFormatIsUnsupported() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Format.fromFileName("image.gif"));

    assertEquals("Unsupported file format: GIF", exception.getMessage());
  }
}
