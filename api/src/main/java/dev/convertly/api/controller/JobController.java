package dev.convertly.api.controller;

import dev.convertly.api.dto.request.CreateJobRequest;
import dev.convertly.api.dto.response.CreateJobResponse;
import dev.convertly.api.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

  private final JobService jobService;

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @PostMapping
  public ResponseEntity<CreateJobResponse> createJob(
      @Valid @RequestBody CreateJobRequest createJobRequest) {
    CreateJobResponse response = jobService.create(createJobRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
