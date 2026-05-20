package com.treco.dex.api.domain.repository;

import com.treco.dex.api.domain.model.ObjectSpecies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ObjectSpeciesRepository extends JpaRepository<ObjectSpecies, UUID> {
    List<ObjectSpecies> findByOwnerId(UUID ownerId);
    Optional<ObjectSpecies> findByOwnerIdAndName(UUID ownerId, String name);
    List<ObjectSpecies> findByPrimaryHabitatId(UUID habitatId);
    List<ObjectSpecies> findByEnvironmentId(UUID environmentId);
}
