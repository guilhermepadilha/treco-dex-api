package com.treco.dex.api.domain.repository;

import com.treco.dex.api.domain.model.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, UUID> {
    List<Environment> findByOwnerId(UUID ownerId);
    Optional<Environment> findByOwnerIdAndName(UUID ownerId, String name);
}
