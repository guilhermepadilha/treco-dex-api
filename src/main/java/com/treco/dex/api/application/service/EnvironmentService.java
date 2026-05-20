package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.CreateEnvironmentRequest;
import com.treco.dex.api.api.dto.EnvironmentResponse;
import com.treco.dex.api.domain.model.Environment;
import com.treco.dex.api.domain.model.UserAccount;
import com.treco.dex.api.domain.repository.EnvironmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EnvironmentService {

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Transactional
    public EnvironmentResponse createEnvironment(CreateEnvironmentRequest request, UUID userId) {
        log.info("[{}] Creating environment: {}", userId, request.getName());

        // Check for duplicate name
        environmentRepository.findByOwnerIdAndName(userId, request.getName())
                .ifPresent(env -> {
                    throw new IllegalArgumentException("Environment with name '" + request.getName() + "' already exists");
                });

        Environment environment = Environment.builder()
                .owner(UserAccount.builder().id(userId).build())
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Environment saved = environmentRepository.save(environment);
        log.info("[{}] Environment created: {}", userId, saved.getId());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public EnvironmentResponse getEnvironment(UUID environmentId, UUID userId) {
        Environment environment = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new IllegalArgumentException("Environment not found"));

        if (!environment.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to environment");
        }

        return toResponse(environment);
    }

    @Transactional(readOnly = true)
    public List<EnvironmentResponse> listEnvironments(UUID userId) {
        return environmentRepository.findByOwnerId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnvironmentResponse updateEnvironment(UUID environmentId, CreateEnvironmentRequest request, UUID userId) {
        Environment environment = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new IllegalArgumentException("Environment not found"));

        if (!environment.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to environment");
        }

        environment.setName(request.getName());
        environment.setDescription(request.getDescription());

        Environment updated = environmentRepository.save(environment);
        log.info("[{}] Environment updated: {}", userId, updated.getId());

        return toResponse(updated);
    }

    @Transactional
    public void deleteEnvironment(UUID environmentId, UUID userId) {
        Environment environment = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new IllegalArgumentException("Environment not found"));

        if (!environment.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to environment");
        }

        environmentRepository.delete(environment);
        log.info("[{}] Environment deleted: {}", userId, environmentId);
    }

    private EnvironmentResponse toResponse(Environment environment) {
        return EnvironmentResponse.builder()
                .id(environment.getId().toString())
                .name(environment.getName())
                .description(environment.getDescription())
                .createdAt(environment.getCreatedAt())
                .updatedAt(environment.getUpdatedAt())
                .build();
    }
}
