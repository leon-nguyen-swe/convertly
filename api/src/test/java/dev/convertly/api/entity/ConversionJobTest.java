package dev.convertly.api.entity;

import static org.junit.jupiter.api.Assertions.*;

import dev.convertly.common.Format;
import dev.convertly.common.JobStatus;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ConversionJobTest {

  private ConversionJob newJob() {
    return new ConversionJob(Format.PNG, Format.PDF, "image.png", 1024);
  }

  private ConversionJob uploadedJob() {
    ConversionJob job = newJob();
    job.assignInputKey("input/image.png");
    job.markUploaded();
    return job;
  }

  private ConversionJob queuedJob() {
    ConversionJob job = uploadedJob();
    job.markQueued();
    return job;
  }

  private ConversionJob runningJob() {
    ConversionJob job = queuedJob();
    job.markRunning();
    return job;
  }

  private ConversionJob doneJob() {
    ConversionJob job = runningJob();
    job.markDone("output/image.pdf");
    return job;
  }

  private ConversionJob failedJob() {
    ConversionJob job = runningJob();
    job.markFailed("Conversion failed");
    return job;
  }

  // ==================== Happy Path ====================

  @Test
  void shouldCompleteJobSuccessfully() {
    ConversionJob job = newJob();

    assertEquals(JobStatus.PENDING, job.getJobStatus());
    assertNull(job.getInputKey());
    assertNull(job.getOutputKey());
    assertNull(job.getStartedAt());
    assertNull(job.getFinishedAt());

    job.assignInputKey("input/image.png");
    assertEquals("input/image.png", job.getInputKey());

    job.markUploaded();
    assertEquals(JobStatus.UPLOADED, job.getJobStatus());

    job.markQueued();
    assertEquals(JobStatus.QUEUED, job.getJobStatus());

    job.markRunning();
    assertEquals(JobStatus.RUNNING, job.getJobStatus());
    assertNotNull(job.getStartedAt());

    Instant startedAt = job.getStartedAt();

    job.markDone("output/image.pdf");

    assertEquals(JobStatus.DONE, job.getJobStatus());
    assertEquals("input/image.png", job.getInputKey());
    assertEquals("output/image.pdf", job.getOutputKey());
    assertEquals(startedAt, job.getStartedAt());
    assertNotNull(job.getFinishedAt());
    assertNull(job.getErrorMessage());
  }

  // ==================== markRunning Retry ====================

  @Test
  void shouldAllowMarkRunningWhenAlreadyRunning() {
    ConversionJob job = runningJob();
    Instant originalStartedAt = job.getStartedAt();

    job.markRunning();

    assertEquals(JobStatus.RUNNING, job.getJobStatus());
    assertEquals(originalStartedAt, job.getStartedAt());
  }

  // ==================== assignInputKey ====================

  @Test
  void shouldThrowWhenAssignInputKeyOnUploadedJob() {
    ConversionJob job = uploadedJob();

    assertThrows(IllegalStateException.class, () -> job.assignInputKey("input/another.png"));
  }

  @Test
  void shouldThrowWhenAssignNullInputKey() {
    ConversionJob job = newJob();

    assertThrows(IllegalArgumentException.class, () -> job.assignInputKey(null));

    assertNull(job.getInputKey());
  }

  @Test
  void shouldThrowWhenAssignBlankInputKey() {
    ConversionJob job = newJob();

    assertThrows(IllegalArgumentException.class, () -> job.assignInputKey("  "));

    assertNull(job.getInputKey());
  }

  // ==================== markUploaded ====================

  @Test
  void shouldThrowWhenMarkUploadedOnUploadedJob() {
    ConversionJob job = uploadedJob();

    assertThrows(IllegalStateException.class, job::markUploaded);
  }

  // ==================== markQueued ====================

  @Test
  void shouldThrowWhenMarkQueuedOnPendingJob() {
    ConversionJob job = newJob();

    assertThrows(IllegalStateException.class, job::markQueued);
  }

  @Test
  void shouldThrowWhenMarkQueuedOnQueuedJob() {
    ConversionJob job = queuedJob();

    assertThrows(IllegalStateException.class, job::markQueued);
  }

  // ==================== markRunning ====================

  @Test
  void shouldThrowWhenMarkRunningOnPendingJob() {
    ConversionJob job = newJob();

    assertThrows(IllegalStateException.class, job::markRunning);
  }

  @Test
  void shouldThrowWhenMarkRunningOnUploadedJob() {
    ConversionJob job = uploadedJob();

    assertThrows(IllegalStateException.class, job::markRunning);
  }

  @Test
  void shouldThrowWhenMarkRunningOnDoneJob() {
    ConversionJob job = doneJob();

    assertThrows(IllegalStateException.class, job::markRunning);
  }

  @Test
  void shouldThrowWhenMarkRunningOnFailedJob() {
    ConversionJob job = failedJob();

    assertThrows(IllegalStateException.class, job::markRunning);
  }

  // ==================== markDone ====================

  @Test
  void shouldThrowWhenMarkDoneOnPendingJob() {
    ConversionJob job = newJob();

    assertThrows(IllegalStateException.class, () -> job.markDone("output/image.pdf"));

    assertEquals(JobStatus.PENDING, job.getJobStatus());
    assertNull(job.getOutputKey());
    assertNull(job.getFinishedAt());
  }

  @Test
  void shouldThrowWhenMarkDoneOnUploadedJob() {
    ConversionJob job = uploadedJob();

    assertThrows(IllegalStateException.class, () -> job.markDone("output/image.pdf"));
  }

  @Test
  void shouldThrowWhenMarkDoneOnQueuedJob() {
    ConversionJob job = queuedJob();

    assertThrows(IllegalStateException.class, () -> job.markDone("output/image.pdf"));
  }

  @Test
  void shouldThrowWhenMarkDoneOnDoneJob() {
    ConversionJob job = doneJob();

    assertThrows(IllegalStateException.class, () -> job.markDone("output/another.pdf"));
  }

  @Test
  void shouldThrowWhenMarkDoneOnFailedJob() {
    ConversionJob job = failedJob();

    assertThrows(IllegalStateException.class, () -> job.markDone("output/image.pdf"));
  }

  @Test
  void shouldThrowWhenMarkDoneWithNullOutputKey() {
    ConversionJob job = runningJob();

    assertThrows(IllegalArgumentException.class, () -> job.markDone(null));

    assertEquals(JobStatus.RUNNING, job.getJobStatus());
    assertNull(job.getOutputKey());
    assertNull(job.getFinishedAt());
  }

  @Test
  void shouldThrowWhenMarkDoneWithBlankOutputKey() {
    ConversionJob job = runningJob();

    assertThrows(IllegalArgumentException.class, () -> job.markDone("  "));

    assertEquals(JobStatus.RUNNING, job.getJobStatus());
    assertNull(job.getOutputKey());
    assertNull(job.getFinishedAt());
  }

  // ==================== markFailed ====================

  @Test
  void shouldMarkJobAsFailed() {
    ConversionJob job = runningJob();

    Instant startedAt = job.getStartedAt();

    job.markFailed("Conversion failed");

    assertEquals(JobStatus.FAILED, job.getJobStatus());
    assertEquals("Conversion failed", job.getErrorMessage());
    assertEquals(startedAt, job.getStartedAt());
    assertNotNull(job.getFinishedAt());
    assertNull(job.getOutputKey());
  }

  @Test
  void shouldThrowWhenMarkFailedOnDoneJob() {
    ConversionJob job = doneJob();
    Instant finishedAt = job.getFinishedAt();

    assertThrows(IllegalStateException.class, () -> job.markFailed("Another error"));

    assertEquals(JobStatus.DONE, job.getJobStatus());
    assertEquals("output/image.pdf", job.getOutputKey());
    assertEquals(finishedAt, job.getFinishedAt());
    assertNull(job.getErrorMessage());
  }

  @Test
  void shouldThrowWhenMarkFailedOnFailedJob() {
    ConversionJob job = failedJob();
    Instant finishedAt = job.getFinishedAt();

    assertThrows(IllegalStateException.class, () -> job.markFailed("Another error"));

    assertEquals(JobStatus.FAILED, job.getJobStatus());
    assertEquals("Conversion failed", job.getErrorMessage());
    assertEquals(finishedAt, job.getFinishedAt());
  }

  @Test
  void shouldAllowMarkFailedBeforeRunning() {
    ConversionJob job = newJob();

    job.markFailed("Upload failed");

    assertEquals(JobStatus.FAILED, job.getJobStatus());
    assertEquals("Upload failed", job.getErrorMessage());
    assertNull(job.getStartedAt());
    assertNotNull(job.getFinishedAt());
  }
}
