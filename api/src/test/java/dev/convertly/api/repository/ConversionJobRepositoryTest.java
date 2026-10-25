package dev.convertly.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import dev.convertly.api.entity.ConversionJob;
import dev.convertly.common.Format;
import dev.convertly.common.JobStatus;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ConversionJobRepositoryTest {

  @Autowired private ConversionJobRepository conversionJobRepository;

  @Autowired private EntityManager entityManager;

  @Test
  void saveAndFindById_shouldPersistAndRetrieveConversionJob() {
    ConversionJob job = new ConversionJob(Format.PNG, Format.PDF, "sample.png", 1024L);

    ConversionJob savedJob = conversionJobRepository.save(job);
    UUID jobId = savedJob.getId();

    entityManager.flush();
    entityManager.clear();

    ConversionJob retrievedJob = conversionJobRepository.findById(jobId).orElseThrow();

    assertThat(retrievedJob.getId()).isEqualTo(jobId);
    assertThat(retrievedJob.getJobStatus()).isEqualTo(JobStatus.PENDING);
    assertThat(retrievedJob.getSourceFormat()).isEqualTo(Format.PNG);
    assertThat(retrievedJob.getTargetFormat()).isEqualTo(Format.PDF);
    assertThat(retrievedJob.getOriginalFileName()).isEqualTo("sample.png");
    assertThat(retrievedJob.getInputSizeBytes()).isEqualTo(1024L);
    assertThat(retrievedJob.getCreatedAt()).isNotNull();
  }
}
