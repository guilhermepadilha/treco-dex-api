package com.treco.dex.api.api.controller;

import com.treco.dex.api.api.dto.CreateObjectStateRequest;
import com.treco.dex.api.api.dto.ObjectStateResponse;
import com.treco.dex.api.application.service.ObjectStateService;
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
@RequestMapping("/api/objects/{objectId}/states")
@Slf4j
public class ObjectStateController {

    @Autowired
    private ObjectStateService objectStateService;

    @PostMapping
    public ResponseEntity<ObjectStateResponse> recordState(
            @PathVariable String objectId,
            @Valid @RequestBody CreateObjectStateRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        ObjectStateResponse response = objectStateService.recordState(UUID.fromString(objectId), request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ObjectStateResponse>> getObjectStates(
            @PathVariable String objectId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<ObjectStateResponse> responses = objectStateService.getObjectStates(UUID.fromString(objectId), userId);
        return ResponseEntity.ok(responses);
    }
}
