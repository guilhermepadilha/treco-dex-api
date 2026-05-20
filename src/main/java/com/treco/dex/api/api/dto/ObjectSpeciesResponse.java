package com.treco.dex.api.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectSpeciesResponse {

    private String id;
    private String name;
    private String description;
    private BigDecimal sizeCm;
    private String primaryHabitatId;
    private String refugeHabitatId;
    private String environmentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
