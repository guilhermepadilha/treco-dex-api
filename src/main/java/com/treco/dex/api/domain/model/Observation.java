package com.treco.dex.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "observation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Observation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_species_id", nullable = false)
    private ObjectSpecies objectSpecies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorder_id", nullable = false)
    private UserAccount recorder;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String note;

    @Column(name = "observed_at", nullable = false)
    private LocalDateTime observedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
