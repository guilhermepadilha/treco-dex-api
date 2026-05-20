package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.ObjectSpeciesResponse;
import com.treco.dex.api.domain.repository.ObjectSpeciesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchService {

    @Autowired
    private ObjectSpeciesRepository objectSpeciesRepository;

    @Transactional(readOnly = true)
    public List<ObjectSpeciesResponse> searchObjectsByName(String name, UUID userId) {
        log.info("[{}] Searching for objects by name: {}", userId, name);

        return objectSpeciesRepository.findByOwnerId(userId).stream()
                .filter(obj -> obj.getName().toLowerCase().contains(name.toLowerCase()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ObjectSpeciesResponse> searchObjectsByHabitat(UUID habitatId, UUID userId) {
        log.info("[{}] Searching for objects by habitat: {}", userId, habitatId);

        return objectSpeciesRepository.findByPrimaryHabitatId(habitatId).stream()
                .filter(obj -> obj.getOwner().getId().equals(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ObjectSpeciesResponse> searchObjectsByEnvironment(UUID environmentId, UUID userId) {
        log.info("[{}] Searching for objects by environment: {}", userId, environmentId);

        return objectSpeciesRepository.findByEnvironmentId(environmentId).stream()
                .filter(obj -> obj.getOwner().getId().equals(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ObjectSpeciesResponse toResponse(Object obj) {
        com.treco.dex.api.domain.model.ObjectSpecies objectSpecies = (com.treco.dex.api.domain.model.ObjectSpecies) obj;
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
