package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.CreateHabitatRequest;
import com.treco.dex.api.api.dto.HabitatResponse;
import com.treco.dex.api.domain.model.Environment;
import com.treco.dex.api.domain.model.Habitat;
import com.treco.dex.api.domain.model.UserAccount;
import com.treco.dex.api.domain.repository.EnvironmentRepository;
import com.treco.dex.api.domain.repository.HabitatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HabitatService {

    @Autowired
    private HabitatRepository habitatRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Transactional
    public HabitatResponse createHabitat(CreateHabitatRequest request, UUID userId) {
        log.info("[{}] Creating habitat: {}", userId, request.getName());

        // Check for duplicate name
        habitatRepository.findByOwnerIdAndName(userId, request.getName())
                .ifPresent(habitat -> {
                    throw new IllegalArgumentException("Habitat with name '" + request.getName() + "' already exists");
                });

        Environment environment = null;
        if (request.getEnvironmentId() != null && !request.getEnvironmentId().isEmpty()) {
            environment = environmentRepository.findById(UUID.fromString(request.getEnvironmentId()))
                    .orElseThrow(() -> new IllegalArgumentException("Environment not found"));
        }

        Habitat habitat = Habitat.builder()
                .owner(UserAccount.builder().id(userId).build())
                .name(request.getName())
                .description(request.getDescription())
                .isPrimary(request.getIsPrimary() != null && request.getIsPrimary())
                .isRefuge(request.getIsRefuge() != null && request.getIsRefuge())
                .environment(environment)
                .build();

        Habitat saved = habitatRepository.save(habitat);
        log.info("[{}] Habitat created: {}", userId, saved.getId());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public HabitatResponse getHabitat(UUID habitatId, UUID userId) {
        Habitat habitat = habitatRepository.findById(habitatId)
                .orElseThrow(() -> new IllegalArgumentException("Habitat not found"));

        if (!habitat.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to habitat");
        }

        return toResponse(habitat);
    }

    @Transactional(readOnly = true)
    public List<HabitatResponse> listHabitats(UUID userId) {
        return habitatRepository.findByOwnerId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public HabitatResponse updateHabitat(UUID habitatId, CreateHabitatRequest request, UUID userId) {
        Habitat habitat = habitatRepository.findById(habitatId)
                .orElseThrow(() -> new IllegalArgumentException("Habitat not found"));

        if (!habitat.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to habitat");
        }

        habitat.setName(request.getName());
        habitat.setDescription(request.getDescription());
        habitat.setIsPrimary(request.getIsPrimary() != null && request.getIsPrimary());
        habitat.setIsRefuge(request.getIsRefuge() != null && request.getIsRefuge());

        Habitat updated = habitatRepository.save(habitat);
        log.info("[{}] Habitat updated: {}", userId, updated.getId());

        return toResponse(updated);
    }

    @Transactional
    public void deleteHabitat(UUID habitatId, UUID userId) {
        Habitat habitat = habitatRepository.findById(habitatId)
                .orElseThrow(() -> new IllegalArgumentException("Habitat not found"));

        if (!habitat.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to habitat");
        }

        habitatRepository.delete(habitat);
        log.info("[{}] Habitat deleted: {}", userId, habitatId);
    }

    private HabitatResponse toResponse(Habitat habitat) {
        return HabitatResponse.builder()
                .id(habitat.getId().toString())
                .name(habitat.getName())
                .description(habitat.getDescription())
                .isPrimary(habitat.getIsPrimary())
                .isRefuge(habitat.getIsRefuge())
                .environmentId(habitat.getEnvironment() != null ? habitat.getEnvironment().getId().toString() : null)
                .createdAt(habitat.getCreatedAt())
                .updatedAt(habitat.getUpdatedAt())
                .build();
    }
}
