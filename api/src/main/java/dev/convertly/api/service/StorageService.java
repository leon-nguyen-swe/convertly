package dev.convertly.api.service;

import dev.convertly.api.config.StorageProperties;
import java.time.Duration;
import java.util.Optional;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class StorageService {

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;
  private final StorageProperties storageProperties;
  private static final Duration PRESIGNED_URL_DURATION = Duration.ofMinutes(15);

  public StorageService(
      S3Client s3Client, S3Presigner s3Presigner, StorageProperties storageProperties) {
    this.s3Client = s3Client;
    this.s3Presigner = s3Presigner;
    this.storageProperties = storageProperties;
  }

  public String presignUpload(String key) {
    return s3Presigner
        .presignPutObject(
            PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_DURATION)
                .putObjectRequest(
                    PutObjectRequest.builder().key(key).bucket(storageProperties.bucket()).build())
                .build())
        .url()
        .toString();
  }

  public String presignDownload(String key) {
    return s3Presigner
        .presignGetObject(
            GetObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_DURATION)
                .getObjectRequest(
                    GetObjectRequest.builder().key(key).bucket(storageProperties.bucket()).build())
                .build())
        .url()
        .toString();
  }

  public Optional<Long> sizeOf(String key) {
    try {
      HeadObjectResponse response =
          s3Client.headObject(
              HeadObjectRequest.builder().key(key).bucket(storageProperties.bucket()).build());

      return Optional.of(response.contentLength());
    } catch (S3Exception e) {
      String errorCode = e.awsErrorDetails() == null ? null : e.awsErrorDetails().errorCode();

      if ("NoSuchKey".equals(errorCode)) {
        return Optional.empty();
      }

      throw e;
    }
  }
}
