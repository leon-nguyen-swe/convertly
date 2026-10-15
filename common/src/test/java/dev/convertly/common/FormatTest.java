package dev.convertly.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
  @ValueSource(strings = {"image.png", "image.PNG", "image.PnG", "document.pdf", "photo.jpg"})
  void fromFileName_shouldSupportDifferentExtensionCases(String fileName) {
    Format actualFormat = Format.fromFileName(fileName);

    assertEquals(
        Format.valueOf(
            fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase(java.util.Locale.ROOT)),
        actualFormat);
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
    assertInstanceOf(IllegalArgumentException.class, exception.getCause());
  }
}
