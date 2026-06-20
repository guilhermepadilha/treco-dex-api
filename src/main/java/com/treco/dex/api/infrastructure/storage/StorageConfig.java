package com.treco.dex.api.infrastructure.storage;

import com.treco.dex.api.application.service.CloudStorageProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;

@Configuration
@Slf4j
public class StorageConfig {

    @Value("${app.storage.aws.bucket:trecodex-media}")
    private String bucket;

    @Value("${app.storage.aws.region:us-east-1}")
    private String region;

    @Value("${app.storage.aws.access-key:minioadmin}")
    private String accessKey;

    @Value("${app.storage.aws.secret-key:minioadmin}")
    private String secretKey;

    @Value("${app.storage.aws.endpoint:#{null}}")
    private String endpoint;
    
    @Value("${app.storage.public-endpoint:#{null}}")
    private String publicEndpoint;

    @Bean
    @ConditionalOnProperty(name = "app.storage.provider", havingValue = "s3", matchIfMissing = true)
    public CloudStorageProvider s3StorageProvider() {
        log.info("Initializing S3 Storage Provider with bucket: {}", bucket);
        
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials));
                
        if (endpoint != null && !endpoint.isEmpty()) {
            log.info("Using custom S3 endpoint: {}", endpoint);
            builder.endpointOverride(URI.create(endpoint));
            // Ensure path style access is used for MinIO
            builder.forcePathStyle(true);
        }
        
        return new S3StorageProvider(builder.build(), bucket, publicEndpoint != null ? publicEndpoint : endpoint);
    }
}
