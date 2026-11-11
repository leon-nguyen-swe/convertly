package dev.convertly.api.dto.request;

import dev.convertly.common.Format;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateJobRequest(
    @NotBlank(message = "File name must not be null or blank") String fileName,
    @NotNull Format sourceFormat,
    @NotNull Format targetFormat,
    @Positive @Max(10_485_760) long size) {}
