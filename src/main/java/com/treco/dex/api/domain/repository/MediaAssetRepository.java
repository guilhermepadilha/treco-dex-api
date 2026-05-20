package com.treco.dex.api.domain.repository;

import com.treco.dex.api.domain.model.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {
    List<MediaAsset> findByObjectSpeciesId(UUID objectSpeciesId);
}
