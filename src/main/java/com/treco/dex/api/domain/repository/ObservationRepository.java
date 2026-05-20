package com.treco.dex.api.domain.repository;

import com.treco.dex.api.domain.model.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, UUID> {
    List<Observation> findByObjectSpeciesIdOrderByObservedAtDesc(UUID objectSpeciesId);
    List<Observation> findByRecorderId(UUID recorderId);
}
