package com.treco.dex.api.api.controller;

import com.treco.dex.api.api.dto.CreateMediaAssetRequest;
import com.treco.dex.api.api.dto.MediaAssetResponse;
import com.treco.dex.api.application.service.MediaStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/objects/{objectId}/media")
@Slf4j
public class MediaController {

    @Autowired
    private MediaStorageService mediaStorageService;

    @PostMapping
    public ResponseEntity<MediaAssetResponse> uploadMedia(
            @PathVariable String objectId,
            @Valid @RequestBody CreateMediaAssetRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        MediaAssetResponse response = mediaStorageService.uploadMediaAsset(UUID.fromString(objectId), request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MediaAssetResponse>> getObjectMedia(
            @PathVariable String objectId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<MediaAssetResponse> responses = mediaStorageService.getObjectMediaAssets(UUID.fromString(objectId), userId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> deleteMedia(
            @PathVariable String objectId,
            @PathVariable String mediaId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        mediaStorageService.deleteMediaAsset(UUID.fromString(mediaId), userId);
        return ResponseEntity.noContent().build();
    }
}
