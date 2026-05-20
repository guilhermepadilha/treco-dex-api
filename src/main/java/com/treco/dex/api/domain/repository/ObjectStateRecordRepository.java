package com.treco.dex.api.domain.repository;

import com.treco.dex.api.domain.model.ObjectStateRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ObjectStateRecordRepository extends JpaRepository<ObjectStateRecord, UUID> {
    List<ObjectStateRecord> findByObjectSpeciesIdOrderByRecordedAtDesc(UUID objectSpeciesId);
    List<ObjectStateRecord> findByRecordedByIdOrderByRecordedAtDesc(UUID recordedById);
}
