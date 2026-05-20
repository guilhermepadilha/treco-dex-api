package com.treco.dex.api.api.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PayloadValidator {

    public void validateObjectSpeciesRequest(String name, String primaryHabitatId, String environmentId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Object species name cannot be empty");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("Object species name exceeds maximum length of 255 characters");
        }
        if (primaryHabitatId == null || primaryHabitatId.trim().isEmpty()) {
            throw new IllegalArgumentException("Primary habitat ID is required");
        }
        if (environmentId == null || environmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Environment ID is required");
        }
    }

    public void validateHabitatRequest(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Habitat name cannot be empty");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("Habitat name exceeds maximum length of 255 characters");
        }
    }

    public void validateObservationRequest(String note) {
        if (note == null || note.trim().isEmpty()) {
            throw new IllegalArgumentException("Observation note cannot be empty");
        }
    }
}
