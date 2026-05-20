package com.treco.dex.api.api.controller;

import com.treco.dex.api.api.dto.CreateObjectSpeciesRequest;
import com.treco.dex.api.api.dto.ObjectSpeciesResponse;
import com.treco.dex.api.application.service.ObjectSpeciesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/objects")
@Slf4j
public class ObjectSpeciesController {

    @Autowired
    private ObjectSpeciesService objectSpeciesService;

    @PostMapping
    public ResponseEntity<ObjectSpeciesResponse> createObjectSpecies(
            @Valid @RequestBody CreateObjectSpeciesRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        ObjectSpeciesResponse response = objectSpeciesService.createObjectSpecies(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjectSpeciesResponse> getObjectSpecies(
            @PathVariable String id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        ObjectSpeciesResponse response = objectSpeciesService.getObjectSpecies(UUID.fromString(id), userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ObjectSpeciesResponse>> listObjectSpecies(
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<ObjectSpeciesResponse> responses = objectSpeciesService.listObjectSpecies(userId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObjectSpeciesResponse> updateObjectSpecies(
            @PathVariable String id,
            @Valid @RequestBody CreateObjectSpeciesRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        ObjectSpeciesResponse response = objectSpeciesService.updateObjectSpecies(UUID.fromString(id), request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObjectSpecies(
            @PathVariable String id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        objectSpeciesService.deleteObjectSpecies(UUID.fromString(id), userId);
        return ResponseEntity.noContent().build();
    }
}
