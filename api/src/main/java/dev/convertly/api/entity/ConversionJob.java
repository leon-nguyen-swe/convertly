package dev.convertly.api.entity;

import dev.convertly.common.Format;
import dev.convertly.common.JobStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Table(name = "conversion_job")
@Entity
public class ConversionJob {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "status")
  private JobStatus jobStatus;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "source_format")
  private Format sourceFormat;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "target_format")
  private Format targetFormat;

  @Column(nullable = false, name = "original_file_name")
  private String originalFileName;

  @Column(nullable = false, name = "input_size_bytes")
  private long inputSizeBytes;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(nullable = false, name = "created_at")
  private Instant createdAt;

  @Column(name = "started_at")
  private Instant startedAt;

  @Column(name = "finished_at")
  private Instant finishedAt;

  @Column(name = "input_key")
  private String inputKey;

  @Column(name = "output_key")
  private String outputKey;

  protected ConversionJob() {}

  public ConversionJob(
      Format sourceFormat, Format targetFormat, String originalFileName, long inputSizeBytes) {
    if (sourceFormat == null) {
      throw new IllegalArgumentException("sourceFormat must not be null");
    }
    if (targetFormat == null) {
      throw new IllegalArgumentException("targetFormat must not be null");
    }
    if (sourceFormat == targetFormat) {
      throw new IllegalArgumentException("Source format and target format must be different");
    }
    if (originalFileName == null || originalFileName.isBlank()) {
      throw new IllegalArgumentException("originalFileName must not be null or blank");
    }
    if (inputSizeBytes <= 0) {
      throw new IllegalArgumentException("inputSizeBytes must be greater than 0");
    }
    this.sourceFormat = sourceFormat;
    this.targetFormat = targetFormat;
    this.originalFileName = originalFileName;
    this.inputSizeBytes = inputSizeBytes;
    this.createdAt = Instant.now();
    this.jobStatus = JobStatus.PENDING;
  }

  public UUID getId() {
    return id;
  }

  public JobStatus getJobStatus() {
    return jobStatus;
  }

  public Format getSourceFormat() {
    return sourceFormat;
  }

  public Format getTargetFormat() {
    return targetFormat;
  }

  public String getOriginalFileName() {
    return originalFileName;
  }

  public long getInputSizeBytes() {
    return inputSizeBytes;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public String getInputKey() {
    return inputKey;
  }

  public String getOutputKey() {
    return outputKey;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public Instant getStartedAt() {
    return startedAt;
  }

  public Instant getFinishedAt() {
    return finishedAt;
  }

  public void assignInputKey(String key) {
    if (this.jobStatus != JobStatus.PENDING) {
      throw new IllegalStateException("JobStatus must be PENDING");
    }
    if (key == null || key.isBlank()) {
      throw new IllegalArgumentException("key must not be null or blank");
    }
    if (this.inputKey != null) {
      throw new IllegalStateException("Input key already assigned");
    }
    this.inputKey = key;
  }

  public void markUploaded() {
    if (this.jobStatus != JobStatus.PENDING) {
      throw new IllegalStateException("JobStatus must be PENDING");
    }
    this.jobStatus = JobStatus.UPLOADED;
  }

  public void markQueued() {
    if (this.jobStatus != JobStatus.UPLOADED) {
      throw new IllegalStateException("JobStatus must be UPLOADED");
    }
    this.jobStatus = JobStatus.QUEUED;
  }

  public void markRunning() {
    if (this.jobStatus != JobStatus.QUEUED && this.jobStatus != JobStatus.RUNNING) {
      throw new IllegalStateException("JobStatus must be QUEUED or RUNNING");
    }
    this.jobStatus = JobStatus.RUNNING;
    if (startedAt == null) {
      this.startedAt = Instant.now();
    }
  }

  public void markDone(String outputKey) {
    if (this.jobStatus != JobStatus.RUNNING) {
      throw new IllegalStateException("JobStatus must be RUNNING");
    }
    if (outputKey == null || outputKey.isBlank()) {
      throw new IllegalArgumentException("outputKey must not be null or blank");
    }
    this.outputKey = outputKey;
    this.jobStatus = JobStatus.DONE;
    this.finishedAt = Instant.now();
  }

  public void markFailed(String message) {
    if (this.jobStatus == JobStatus.DONE || this.jobStatus == JobStatus.FAILED) {
      throw new IllegalStateException(
          "Cannot mark Failed state with Job status: " + this.jobStatus);
    }
    this.jobStatus = JobStatus.FAILED;
    this.errorMessage = message;
    this.finishedAt = Instant.now();
  }
}
