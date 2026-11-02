package dev.convertly.common;

import java.util.UUID;

public record ConversionMessage(
    UUID jobId, String inputKey, String outputKey, Format from, Format to) {}
