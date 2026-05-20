package com.treco.dex.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "media_asset")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_species_id", nullable = false)
    private ObjectSpecies objectSpecies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upload_by_id", nullable = false)
    private UserAccount uploadedBy;

    @Column(nullable = false)
    private String url;

    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
