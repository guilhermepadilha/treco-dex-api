package com.treco.dex.api.infrastructure.telemetry;

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
public class AuditEvent {

    private String correlationId;
    private String userId;
    private String action;
    private String resourceType;
    private String resourceId;
    private String details;
    private LocalDateTime timestamp;
    private boolean success;
}
