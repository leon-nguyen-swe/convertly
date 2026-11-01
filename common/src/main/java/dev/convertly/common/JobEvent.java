package dev.convertly.common;

import java.util.UUID;

public record JobEvent(UUID jobId, JobStatus status, String errorMessage) {}
