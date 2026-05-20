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
public class HabitatResponse {

    private String id;
    private String name;
    private String description;
    private Boolean isPrimary;
    private Boolean isRefuge;
    private String environmentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
