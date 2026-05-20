package com.treco.dex.api.api.controller;

import com.treco.dex.api.api.dto.CreateHabitatRequest;
import com.treco.dex.api.api.dto.HabitatResponse;
import com.treco.dex.api.application.service.HabitatService;
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
@RequestMapping("/api/habitats")
@Slf4j
public class HabitatController {

    @Autowired
    private HabitatService habitatService;

    @PostMapping
    public ResponseEntity<HabitatResponse> createHabitat(
            @Valid @RequestBody CreateHabitatRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        HabitatResponse response = habitatService.createHabitat(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitatResponse> getHabitat(
            @PathVariable String id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        HabitatResponse response = habitatService.getHabitat(UUID.fromString(id), userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<HabitatResponse>> listHabitats(
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<HabitatResponse> responses = habitatService.listHabitats(userId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitatResponse> updateHabitat(
            @PathVariable String id,
            @Valid @RequestBody CreateHabitatRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        HabitatResponse response = habitatService.updateHabitat(UUID.fromString(id), request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabitat(
            @PathVariable String id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        habitatService.deleteHabitat(UUID.fromString(id), userId);
        return ResponseEntity.noContent().build();
    }
}
