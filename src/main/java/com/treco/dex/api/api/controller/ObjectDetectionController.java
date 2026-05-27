package com.treco.dex.api.api.controller;

import com.treco.dex.api.api.dto.VisualSearchResponse;
import com.treco.dex.api.application.service.VisionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/objects")
@Slf4j
public class ObjectDetectionController {

    private final VisionService visionService;

    public ObjectDetectionController(VisionService visionService) {
        this.visionService = visionService;
    }

    @PostMapping(value = "/visual-search", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VisualSearchResponse> visualSearch(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication) throws IOException {
        UUID userId = UUID.fromString(authentication.getName());
        MultipartFile activeFile = file != null ? file : image;
        if (activeFile == null) {
            log.error("[{}] Missing both 'file' and 'image' parts in multipart request", userId);
            throw new IllegalArgumentException("Required request part 'file' or 'image' is not present");
        }
        
        log.info("[{}] Visual search endpoint invoked with file: {}", userId, activeFile.getOriginalFilename());
        
        byte[] imageBytes = activeFile.getBytes();
        VisualSearchResponse response = visionService.searchByImage(imageBytes, userId);
        return ResponseEntity.ok(response);
    }
}
