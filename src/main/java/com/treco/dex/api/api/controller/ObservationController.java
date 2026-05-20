package com.treco.dex.api.api.controller;

import com.treco.dex.api.api.dto.CreateObservationRequest;
import com.treco.dex.api.api.dto.ObservationResponse;
import com.treco.dex.api.application.service.ObservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/objects/{objectId}/observations")
@Slf4j
public class ObservationController {

    @Autowired
    private ObservationService observationService;

    @PostMapping
    public ResponseEntity<Void> recordObservation(
            @PathVariable String objectId,
            @Valid @RequestBody CreateObservationRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        observationService.recordObservation(UUID.fromString(objectId), request.getNote(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
