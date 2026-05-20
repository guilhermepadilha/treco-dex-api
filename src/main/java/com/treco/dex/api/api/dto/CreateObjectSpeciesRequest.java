package com.treco.dex.api.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateObjectSpeciesRequest {

    @NotBlank(message = "Object species name is required")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;

    private String description;

    private BigDecimal sizeCm;

    @NotBlank(message = "Primary habitat ID is required")
    private String primaryHabitatId;

    private String refugeHabitatId;

    @NotBlank(message = "Environment ID is required")
    private String environmentId;
}
