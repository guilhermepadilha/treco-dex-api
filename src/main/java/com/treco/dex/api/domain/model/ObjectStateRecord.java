package com.treco.dex.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "object_state_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectStateRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_species_id", nullable = false)
    private ObjectSpecies objectSpecies;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ObjectState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_id", nullable = false)
    private UserAccount recordedBy;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ObjectState {
        UNKNOWN,
        FOUND,
        MISSING,
        RELOCATED
    }
}
