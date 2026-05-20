package com.treco.dex.api.api.controller;

import com.treco.dex.api.application.service.RecommendationResult;
import com.treco.dex.api.application.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/objects")
@Slf4j
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<RecommendationResult> recommendHabitat(
            @RequestParam("objectName") String objectName,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        log.info("[{}] Habitat recommendation endpoint invoked for object: {}", userId, objectName);
        RecommendationResult response = recommendationService.recommend(objectName, userId);
        return ResponseEntity.ok(response);
    }
}
