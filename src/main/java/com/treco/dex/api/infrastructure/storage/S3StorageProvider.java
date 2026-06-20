package com.treco.dex.api.infrastructure.storage;

import com.treco.dex.api.application.service.CloudStorageProvider;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Base64;

@Slf4j
public class S3StorageProvider implements CloudStorageProvider {

    private final S3Client s3Client;
    private final String bucketName;
    private final String publicEndpoint;

    public S3StorageProvider(S3Client s3Client, String bucketName, String publicEndpoint) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.publicEndpoint = publicEndpoint;
    }

    @Override
    public String uploadBase64Image(String base64Content, String fileName, String mediaType) {
        try {
            // Remove the data URL prefix if present (e.g., "data:image/jpeg;base64,")
            String base64Data = base64Content;
            if (base64Content.contains(",")) {
                base64Data = base64Content.split(",")[1];
            }
            
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(mediaType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));

            // Generate URL
            if (publicEndpoint != null && !publicEndpoint.isEmpty()) {
                return publicEndpoint + "/" + bucketName + "/" + fileName;
            } else {
                return s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(bucketName).key(fileName).build()).toString();
            }
        } catch (Exception e) {
            log.error("Failed to upload image to S3", e);
            throw new RuntimeException("Failed to upload image to S3", e);
        }
    }

    @Override
    public void deleteImage(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("Failed to delete image from S3: {}", fileUrl, e);
        }
    }
}
