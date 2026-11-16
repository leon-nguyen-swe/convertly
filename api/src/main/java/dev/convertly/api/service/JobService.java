package dev.convertly.api.service;

import dev.convertly.api.dto.request.CreateJobRequest;
import dev.convertly.api.dto.response.CreateJobResponse;
import dev.convertly.api.dto.response.JobResponse;
import dev.convertly.api.entity.ConversionJob;
import dev.convertly.api.exception.JobNotFoundException;
import dev.convertly.api.repository.ConversionJobRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {

  private final ConversionJobRepository repository;
  private final StorageService storageService;

  public JobService(
      ConversionJobRepository conversionJobRepository, StorageService storageService) {
    this.repository = conversionJobRepository;
    this.storageService = storageService;
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
}
