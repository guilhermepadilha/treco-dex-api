package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.ObjectSpeciesResponse;
import com.treco.dex.api.domain.model.*;
import com.treco.dex.api.domain.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class ObservationService {

    @Autowired
    private ObservationRepository observationRepository;

    @Autowired
    private ObjectSpeciesRepository objectSpeciesRepository;

    @Transactional
    public void recordObservation(UUID objectSpeciesId, String note, UUID recorderId) {
        log.info("[{}] Recording observation for object: {}", recorderId, objectSpeciesId);

        ObjectSpecies objectSpecies = objectSpeciesRepository.findById(objectSpeciesId)
                .orElseThrow(() -> new IllegalArgumentException("Object species not found"));

        Observation observation = Observation.builder()
                .objectSpecies(objectSpecies)
                .recorder(UserAccount.builder().id(recorderId).build())
                .note(note)
                .observedAt(LocalDateTime.now())
                .build();

        observationRepository.save(observation);
        log.info("[{}] Observation recorded: {}", recorderId, observation.getId());
    }
}
