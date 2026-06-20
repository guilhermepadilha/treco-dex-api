package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.CreateMediaAssetRequest;
import com.treco.dex.api.api.dto.MediaAssetResponse;
import com.treco.dex.api.domain.model.MediaAsset;
import com.treco.dex.api.domain.model.ObjectSpecies;
import com.treco.dex.api.domain.model.UserAccount;
import com.treco.dex.api.domain.repository.MediaAssetRepository;
import com.treco.dex.api.domain.repository.ObjectSpeciesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MediaStorageService {

    @Autowired
    private MediaAssetRepository mediaAssetRepository;

    @Autowired
    private ObjectSpeciesRepository objectSpeciesRepository;

    @Autowired
    private CloudStorageProvider cloudStorageProvider;

    @Transactional
    public MediaAssetResponse uploadMediaAsset(UUID objectSpeciesId, CreateMediaAssetRequest request, UUID userId) {
        log.info("[{}] Uploading media asset for object: {}", userId, objectSpeciesId);

        ObjectSpecies objectSpecies = objectSpeciesRepository.findById(objectSpeciesId)
                .orElseThrow(() -> new IllegalArgumentException("Object species not found"));

        if (!objectSpecies.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to object species");
        }

        // Determine file extension from media type
        String extension = ".bin";
        if (request.getMediaType() != null) {
            if (request.getMediaType().contains("jpeg") || request.getMediaType().contains("jpg")) extension = ".jpg";
            else if (request.getMediaType().contains("png")) extension = ".png";
            else if (request.getMediaType().contains("gif")) extension = ".gif";
            else if (request.getMediaType().contains("webp")) extension = ".webp";
        }
        
        String fileName = UUID.randomUUID().toString() + extension;
        String uploadedUrl = cloudStorageProvider.uploadBase64Image(request.getUrl(), fileName, request.getMediaType());

        MediaAsset mediaAsset = MediaAsset.builder()
                .objectSpecies(objectSpecies)
                .uploadedBy(UserAccount.builder().id(userId).build())
                .url(uploadedUrl)
                .mediaType(request.getMediaType())
                .build();

        MediaAsset saved = mediaAssetRepository.save(mediaAsset);
        log.info("[{}] Media asset uploaded: {}", userId, saved.getId());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MediaAssetResponse> getObjectMediaAssets(UUID objectSpeciesId, UUID userId) {
        ObjectSpecies objectSpecies = objectSpeciesRepository.findById(objectSpeciesId)
                .orElseThrow(() -> new IllegalArgumentException("Object species not found"));

        if (!objectSpecies.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to object species");
        }

        return mediaAssetRepository.findByObjectSpeciesId(objectSpeciesId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMediaAsset(UUID mediaAssetId, UUID userId) {
        MediaAsset mediaAsset = mediaAssetRepository.findById(mediaAssetId)
                .orElseThrow(() -> new IllegalArgumentException("Media asset not found"));

        if (!mediaAsset.getUploadedBy().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to media asset");
        }

        cloudStorageProvider.deleteImage(mediaAsset.getUrl());
        mediaAssetRepository.delete(mediaAsset);
        log.info("[{}] Media asset deleted: {}", userId, mediaAssetId);
    }

    private MediaAssetResponse toResponse(MediaAsset mediaAsset) {
        return MediaAssetResponse.builder()
                .id(mediaAsset.getId().toString())
                .objectSpeciesId(mediaAsset.getObjectSpecies().getId().toString())
                .url(mediaAsset.getUrl())
                .mediaType(mediaAsset.getMediaType())
                .createdAt(mediaAsset.getCreatedAt())
                .build();
    }
}
