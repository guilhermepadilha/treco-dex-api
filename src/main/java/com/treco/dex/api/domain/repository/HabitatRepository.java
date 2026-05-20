package com.treco.dex.api.domain.repository;

import com.treco.dex.api.domain.model.Habitat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HabitatRepository extends JpaRepository<Habitat, UUID> {
    List<Habitat> findByOwnerId(UUID ownerId);
    Optional<Habitat> findByOwnerIdAndName(UUID ownerId, String name);
    List<Habitat> findByEnvironmentId(UUID environmentId);
}
