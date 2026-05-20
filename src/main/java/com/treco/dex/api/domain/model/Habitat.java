package com.treco.dex.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "habitat", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"owner_id", "name"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Habitat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserAccount owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "environment_id")
    private Environment environment;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "is_refuge")
    private Boolean isRefuge;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isPrimary == null) isPrimary = false;
        if (isRefuge == null) isRefuge = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
