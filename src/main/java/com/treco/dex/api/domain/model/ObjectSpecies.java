package com.treco.dex.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "object_species", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"owner_id", "name"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectSpecies {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserAccount owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_habitat_id", nullable = false)
    private Habitat primaryHabitat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refuge_habitat_id")
    private Habitat refugeHabitat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "environment_id", nullable = false)
    private Environment environment;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "size_cm")
    private BigDecimal sizeCm;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
