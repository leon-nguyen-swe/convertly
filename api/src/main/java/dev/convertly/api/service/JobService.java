package dev.convertly.api.service;

import dev.convertly.api.dto.request.CreateJobRequest;
import dev.convertly.api.dto.response.CreateJobResponse;
import dev.convertly.api.dto.response.JobResponse;
import dev.convertly.api.entity.ConversionJob;
import dev.convertly.api.exception.FileNotUploadedException;
import dev.convertly.api.exception.FileTooLargeException;
import dev.convertly.api.exception.JobNotFoundException;
import dev.convertly.api.repository.ConversionJobRepository;
import dev.convertly.common.ConversionMessage;
import dev.convertly.common.Queues;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {

  private final ConversionJobRepository repository;
  private final StorageService storageService;
  private final RabbitTemplate rabbitTemplate;
  private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

  public JobService(
      ConversionJobRepository conversionJobRepository,
      StorageService storageService,
      RabbitTemplate rabbitTemplate) {
    this.repository = conversionJobRepository;
    this.storageService = storageService;
    this.rabbitTemplate = rabbitTemplate;
  }

  @Transactional
  public CreateJobResponse create(CreateJobRequest request) {
    ConversionJob job =
        new ConversionJob(
            request.sourceFormat(), request.targetFormat(), request.fileName(), request.size());

    repository.save(job);

    job.assignInputKey("jobs/" + job.getId() + "/input");

    String uploadUrl = storageService.presignUpload(job.getInputKey());

    return new CreateJobResponse(job.getId(), uploadUrl);
  }

  @Transactional(readOnly = true)
  public JobResponse get(UUID id) {
    ConversionJob job = repository.findById(id).orElseThrow(() -> new JobNotFoundException(id));

    return JobResponse.from(job);
  }

  public void start(UUID id) {
    ConversionJob job = repository.findById(id).orElseThrow(() -> new JobNotFoundException(id));

    Optional<Long> actualFileSize = storageService.sizeOf(job.getInputKey());

    if (actualFileSize.isEmpty()) {
      throw new FileNotUploadedException(id);
    }

    if (actualFileSize.get() > MAX_FILE_SIZE) {
      throw new FileTooLargeException(id);
    }

    job.markUploaded();
    job.markQueued();
    repository.save(job);

    String outputKey =
        "jobs/" + job.getId() + "/output." + job.getTargetFormat().name().toLowerCase(Locale.ROOT);
    ConversionMessage message =
        new ConversionMessage(
            job.getId(),
            job.getInputKey(),
            outputKey,
            job.getSourceFormat(),
            job.getTargetFormat());

    try {
      rabbitTemplate.convertAndSend(Queues.JOBS, message);
    } catch (AmqpException e) {
      job.markFailed("Could not queue the job");
      repository.save(job);
      throw e;
    }
  }
}
