package com.treco.dex.api.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMediaAssetRequest {

    @NotBlank(message = "Media URL is required")
    private String url;

    @NotBlank(message = "Media type is required")
    private String mediaType; // e.g., "image/jpeg", "image/png"
}
