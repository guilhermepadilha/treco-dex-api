package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.CreateObjectStateRequest;
import com.treco.dex.api.api.dto.ObjectStateResponse;
import com.treco.dex.api.domain.model.*;
import com.treco.dex.api.domain.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ObjectStateService {

    @Autowired
    private ObjectStateRecordRepository objectStateRecordRepository;

    @Autowired
    private ObjectSpeciesRepository objectSpeciesRepository;

    @Transactional
    public ObjectStateResponse recordState(UUID objectSpeciesId, CreateObjectStateRequest request, UUID recordedById) {
        log.info("[{}] Recording state for object: {} -> {}", recordedById, objectSpeciesId, request.getState());

        ObjectSpecies objectSpecies = objectSpeciesRepository.findById(objectSpeciesId)
                .orElseThrow(() -> new IllegalArgumentException("Object species not found"));

        ObjectStateRecord.ObjectState state;
        try {
            state = ObjectStateRecord.ObjectState.valueOf(request.getState().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid state: " + request.getState());
        }

        ObjectStateRecord stateRecord = ObjectStateRecord.builder()
                .objectSpecies(objectSpecies)
                .state(state)
                .recordedBy(UserAccount.builder().id(recordedById).build())
                .recordedAt(LocalDateTime.now())
                .build();

        ObjectStateRecord saved = objectStateRecordRepository.save(stateRecord);
        log.info("[{}] State record created: {}", recordedById, saved.getId());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ObjectStateResponse> getObjectStates(UUID objectSpeciesId, UUID userId) {
        ObjectSpecies objectSpecies = objectSpeciesRepository.findById(objectSpeciesId)
                .orElseThrow(() -> new IllegalArgumentException("Object species not found"));

        if (!objectSpecies.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to object species");
        }

        return objectStateRecordRepository.findByObjectSpeciesIdOrderByRecordedAtDesc(objectSpeciesId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ObjectStateResponse toResponse(ObjectStateRecord stateRecord) {
        return ObjectStateResponse.builder()
                .id(stateRecord.getId().toString())
                .objectSpeciesId(stateRecord.getObjectSpecies().getId().toString())
                .state(stateRecord.getState().toString())
                .recordedById(stateRecord.getRecordedBy().getId().toString())
                .recordedAt(stateRecord.getRecordedAt())
                .createdAt(stateRecord.getCreatedAt())
                .build();
    }
}
