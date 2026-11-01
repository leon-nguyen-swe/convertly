package dev.convertly.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class MessageContractTest {

  @Test
  void shouldResolveQueuedStatus() {
    assertEquals(JobStatus.QUEUED, JobStatus.valueOf("QUEUED"));
  }

  @Test
  void conversionMessageShouldCompareByValue() {
    UUID jobId = UUID.randomUUID();

    ConversionMessage message1 =
        new ConversionMessage(jobId, "input/file.png", "output/file.pdf", Format.PNG, Format.PDF);

    ConversionMessage message2 =
        new ConversionMessage(jobId, "input/file.png", "output/file.pdf", Format.PNG, Format.PDF);

    assertNotSame(message1, message2);
    assertEquals(message1, message2);
  }

  @Test
  void jobEventShouldCompareByValue() {
    UUID jobId = UUID.randomUUID();

    JobEvent event1 = new JobEvent(jobId, JobStatus.DONE, null);

    JobEvent event2 = new JobEvent(jobId, JobStatus.DONE, null);

    assertNotSame(event1, event2);
    assertEquals(event1, event2);
  }
}
