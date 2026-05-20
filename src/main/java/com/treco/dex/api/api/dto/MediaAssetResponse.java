package com.treco.dex.api.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaAssetResponse {

    private String id;
    private String objectSpeciesId;
    private String url;
    private String mediaType;
    private LocalDateTime createdAt;
}
