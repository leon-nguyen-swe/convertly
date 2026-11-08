package dev.convertly.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.convertly.api.config.StorageProperties;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

  private static final String BUCKET = "convertly";
  private static final String KEY = "test/hello.txt";

  @Mock private S3Client s3Client;

  @Mock private S3Presigner s3Presigner;

  private StorageService storageService;

  @BeforeEach
  void setUp() {
    StorageProperties properties =
        new StorageProperties(
            "http://localhost:9000", BUCKET, "test-access-key", "test-secret-key");

    storageService = new StorageService(s3Client, s3Presigner, properties);
  }

  @Test
  void sizeOf_shouldReturnFileSize_whenObjectExists() {
    // Arrange
    long expectedSize = 1024L;

    HeadObjectResponse response = HeadObjectResponse.builder().contentLength(expectedSize).build();

    when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(response);

    // Act
    Optional<Long> result = storageService.sizeOf(KEY);

    // Assert
    assertEquals(Optional.of(expectedSize), result);

    verify(s3Client).headObject(HeadObjectRequest.builder().bucket(BUCKET).key(KEY).build());
  }

  @Test
  void sizeOf_shouldReturnEmpty_whenObjectDoesNotExist() {
    // Arrange
    S3Exception exception =
        (S3Exception)
            S3Exception.builder()
                .message("The specified key does not exist.")
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("NoSuchKey").build())
                .build();

    when(s3Client.headObject(any(HeadObjectRequest.class))).thenThrow(exception);

    // Act
    Optional<Long> result = storageService.sizeOf(KEY);

    // Assert
    assertTrue(result.isEmpty());

    verify(s3Client).headObject(HeadObjectRequest.builder().bucket(BUCKET).key(KEY).build());
  }

  @Test
  void sizeOf_shouldRethrowException_whenS3ReturnsUnexpectedError() {
    // Arrange
    S3Exception exception =
        (S3Exception)
            S3Exception.builder()
                .message("Access denied.")
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("AccessDenied").build())
                .build();

    when(s3Client.headObject(any(HeadObjectRequest.class))).thenThrow(exception);

    // Act & Assert
    S3Exception thrown = assertThrows(S3Exception.class, () -> storageService.sizeOf(KEY));

    assertSame(exception, thrown);

    verify(s3Client).headObject(HeadObjectRequest.builder().bucket(BUCKET).key(KEY).build());
  }
}
