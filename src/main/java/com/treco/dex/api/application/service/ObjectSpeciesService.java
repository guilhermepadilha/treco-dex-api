package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.CreateObjectSpeciesRequest;
import com.treco.dex.api.api.dto.ObjectSpeciesResponse;
import com.treco.dex.api.domain.model.*;
import com.treco.dex.api.domain.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ObjectSpeciesService {

    @Autowired
    private ObjectSpeciesRepository objectSpeciesRepository;

    @Autowired
    private HabitatRepository habitatRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Transactional
    public ObjectSpeciesResponse createObjectSpecies(CreateObjectSpeciesRequest request, UUID userId) {
        log.info("[{}] Creating object species: {}", userId, request.getName());

        // Validate habitats and environment exist
        Habitat primaryHabitat = habitatRepository.findById(UUID.fromString(request.getPrimaryHabitatId()))
                .orElseThrow(() -> new IllegalArgumentException("Primary habitat not found"));

        Habitat refugeHabitat = null;
        if (request.getRefugeHabitatId() != null && !request.getRefugeHabitatId().isEmpty()) {
            refugeHabitat = habitatRepository.findById(UUID.fromString(request.getRefugeHabitatId()))
                    .orElseThrow(() -> new IllegalArgumentException("Refuge habitat not found"));
        }

        Environment environment = environmentRepository.findById(UUID.fromString(request.getEnvironmentId()))
                .orElseThrow(() -> new IllegalArgumentException("Environment not found"));

        // Check for duplicate name
        objectSpeciesRepository.findByOwnerIdAndName(userId, request.getName())
                .ifPresent(obj -> {
                    throw new IllegalArgumentException("Object species with name '" + request.getName() + "' already exists");
                });

        ObjectSpecies objectSpecies = ObjectSpecies.builder()
                .owner(UserAccount.builder().id(userId).build())
                .name(request.getName())
                .description(request.getDescription())
                .sizeCm(request.getSizeCm())
                .primaryHabitat(primaryHabitat)
                .refugeHabitat(refugeHabitat)
                .environment(environment)
                .build();

        ObjectSpecies saved = objectSpeciesRepository.save(objectSpecies);
        log.info("[{}] Object species created: {}", userId, saved.getId());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ObjectSpeciesResponse getObjectSpecies(UUID objectSpeciesId, UUID userId) {
        ObjectSpecies objectSpecies = objectSpeciesRepository.findById(objectSpeciesId)
                .orElseThrow(() -> new IllegalArgumentException("Object species not found"));

        if (!objectSpecies.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to object species");
        }

        return toResponse(objectSpecies);
    }

    @Transactional(readOnly = true)
    public List<ObjectSpeciesResponse> listObjectSpecies(UUID userId) {
        return objectSpeciesRepository.findByOwnerId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ObjectSpeciesResponse updateObjectSpecies(UUID objectSpeciesId, CreateObjectSpeciesRequest request, UUID userId) {
        ObjectSpecies objectSpecies = objectSpeciesRepository.findById(objectSpeciesId)
                .orElseThrow(() -> new IllegalArgumentException("Object species not found"));

        if (!objectSpecies.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to object species");
        }

        Habitat primaryHabitat = habitatRepository.findById(UUID.fromString(request.getPrimaryHabitatId()))
                .orElseThrow(() -> new IllegalArgumentException("Primary habitat not found"));

        objectSpecies.setName(request.getName());
        objectSpecies.setDescription(request.getDescription());
        objectSpecies.setSizeCm(request.getSizeCm());
        objectSpecies.setPrimaryHabitat(primaryHabitat);

        if (request.getRefugeHabitatId() != null && !request.getRefugeHabitatId().isEmpty()) {
            Habitat refugeHabitat = habitatRepository.findById(UUID.fromString(request.getRefugeHabitatId()))
                    .orElseThrow(() -> new IllegalArgumentException("Refuge habitat not found"));
            objectSpecies.setRefugeHabitat(refugeHabitat);
        }

        ObjectSpecies updated = objectSpeciesRepository.save(objectSpecies);
        log.info("[{}] Object species updated: {}", userId, updated.getId());

        return toResponse(updated);
    }

    @Transactional
    public void deleteObjectSpecies(UUID objectSpeciesId, UUID userId) {
        ObjectSpecies objectSpecies = objectSpeciesRepository.findById(objectSpeciesId)
                .orElseThrow(() -> new IllegalArgumentException("Object species not found"));

        if (!objectSpecies.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to object species");
        }

        objectSpeciesRepository.delete(objectSpecies);
        log.info("[{}] Object species deleted: {}", userId, objectSpeciesId);
    }

    private ObjectSpeciesResponse toResponse(ObjectSpecies objectSpecies) {
        return ObjectSpeciesResponse.builder()
                .id(objectSpecies.getId().toString())
                .name(objectSpecies.getName())
                .description(objectSpecies.getDescription())
                .sizeCm(objectSpecies.getSizeCm())
                .primaryHabitatId(objectSpecies.getPrimaryHabitat().getId().toString())
                .refugeHabitatId(objectSpecies.getRefugeHabitat() != null ? objectSpecies.getRefugeHabitat().getId().toString() : null)
                .environmentId(objectSpecies.getEnvironment().getId().toString())
                .createdAt(objectSpecies.getCreatedAt())
                .updatedAt(objectSpecies.getUpdatedAt())
                .build();
    }
}
