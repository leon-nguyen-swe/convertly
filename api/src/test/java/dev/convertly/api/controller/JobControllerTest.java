package dev.convertly.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.convertly.api.dto.request.CreateJobRequest;
import dev.convertly.api.dto.response.CreateJobResponse;
import dev.convertly.api.service.JobService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = JobController.class)
class JobControllerTest {

  private static final String API_URL = "/api/v1/jobs";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private JobService jobService;

  @Test
  void createJob_shouldReturn201_whenRequestIsValid() throws Exception {
    // Arrange
    UUID jobId = UUID.randomUUID();
    String uploadUrl = "http://localhost:9000/convertly/jobs/test/input";

    CreateJobResponse response = new CreateJobResponse(jobId, uploadUrl);

    when(jobService.create(any(CreateJobRequest.class))).thenReturn(response);

    String requestBody =
        """
            {
                "fileName": "image.png",
                "sourceFormat": "PNG",
                "targetFormat": "PDF",
                "size": 102400
            }
            """;

    // Act & Assert
    mockMvc
        .perform(post(API_URL).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.jobId").value(jobId.toString()))
        .andExpect(jsonPath("$.uploadUrl").value(uploadUrl));

    verify(jobService).create(any(CreateJobRequest.class));
  }

  @Test
  void createJob_shouldReturn400_whenFileNameIsMissing() throws Exception {
    // Arrange
    String requestBody =
        """
            {
                "sourceFormat": "PNG",
                "targetFormat": "PDF",
                "size": 102400
            }
            """;

    // Act & Assert
    mockMvc
        .perform(post(API_URL).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

    verifyNoInteractions(jobService);
  }

  @Test
  void createJob_shouldReturn400_whenFileSizeExceedsLimit() throws Exception {
    // Arrange
    String requestBody =
        """
            {
                "fileName": "large-image.png",
                "sourceFormat": "PNG",
                "targetFormat": "PDF",
                "size": 10485761
            }
            """;

    // Act & Assert
    mockMvc
        .perform(post(API_URL).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

    verifyNoInteractions(jobService);
  }
}
