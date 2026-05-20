package com.treco.dex.api.api.controller;

import com.treco.dex.api.api.dto.VisualSearchResponse;
import com.treco.dex.api.application.service.VisionService;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/visual-search")
    public ResponseEntity<VisualSearchResponse> visualSearch(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {
        UUID userId = UUID.fromString(authentication.getName());
        log.info("[{}] Visual search endpoint invoked with file: {}", userId, file.getOriginalFilename());
        
        byte[] imageBytes = file.getBytes();
        VisualSearchResponse response = visionService.searchByImage(imageBytes, userId);
        return ResponseEntity.ok(response);
    }
}
