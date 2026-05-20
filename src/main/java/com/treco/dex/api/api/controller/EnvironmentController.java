package com.treco.dex.api.api.controller;

import com.treco.dex.api.api.dto.CreateEnvironmentRequest;
import com.treco.dex.api.api.dto.EnvironmentResponse;
import com.treco.dex.api.application.service.EnvironmentService;
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
@RequestMapping("/api/environments")
@Slf4j
public class EnvironmentController {

    @Autowired
    private EnvironmentService environmentService;

    @PostMapping
    public ResponseEntity<EnvironmentResponse> createEnvironment(
            @Valid @RequestBody CreateEnvironmentRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        EnvironmentResponse response = environmentService.createEnvironment(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvironmentResponse> getEnvironment(
            @PathVariable String id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        EnvironmentResponse response = environmentService.getEnvironment(UUID.fromString(id), userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EnvironmentResponse>> listEnvironments(
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<EnvironmentResponse> responses = environmentService.listEnvironments(userId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnvironmentResponse> updateEnvironment(
            @PathVariable String id,
            @Valid @RequestBody CreateEnvironmentRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        EnvironmentResponse response = environmentService.updateEnvironment(UUID.fromString(id), request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnvironment(
            @PathVariable String id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        environmentService.deleteEnvironment(UUID.fromString(id), userId);
        return ResponseEntity.noContent().build();
    }
}
