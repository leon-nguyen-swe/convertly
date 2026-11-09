package dev.convertly.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("convertly.storage")
public record StorageProperties(
    String endpoint, String bucket, String accessKey, String secretKey) {}
