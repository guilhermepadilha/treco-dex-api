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
public class ObservationResponse {

    private String id;
    private String objectSpeciesId;
    private String recorderId;
    private String note;
    private LocalDateTime observedAt;
    private LocalDateTime createdAt;
}
