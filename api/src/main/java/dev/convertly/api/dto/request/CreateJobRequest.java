package dev.convertly.api.dto.request;

import dev.convertly.common.Format;
import jakarta.validation.constraints.*;

public record CreateJobRequest(
    @NotBlank(message = "File name must not be null or blank") String fileName,
    @NotNull Format sourceFormat,
    @NotNull Format targetFormat,
    @Positive @Max(10_485_760) long size) {

    @AssertTrue(message = "Source and target format must be different")
    public boolean isDifferentFormats() {
        return sourceFormat != targetFormat;
    }
}
