package com.treco.dex.api.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateObjectStateRequest {

    @NotBlank(message = "State is required")
    private String state; // UNKNOWN, FOUND, MISSING, RELOCATED
}
