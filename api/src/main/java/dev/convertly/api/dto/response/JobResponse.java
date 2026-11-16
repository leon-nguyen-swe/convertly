package dev.convertly.api.dto.response;

import dev.convertly.api.entity.ConversionJob;
import dev.convertly.common.Format;
import dev.convertly.common.JobStatus;
import java.time.Instant;
import java.util.UUID;

public record JobResponse(
    UUID jobId,
    JobStatus status,
    Format sourceFormat,
    Format targetFormat,
    String originalFilename,
    String errorMessage,
    Instant createdAt,
    Instant startedAt) {

  public static JobResponse from(ConversionJob job) {
    return new JobResponse(
        job.getId(),
        job.getJobStatus(),
        job.getSourceFormat(),
        job.getTargetFormat(),
        job.getOriginalFileName(),
        job.getErrorMessage(),
        job.getCreatedAt(),
        job.getStartedAt());
  }
}
