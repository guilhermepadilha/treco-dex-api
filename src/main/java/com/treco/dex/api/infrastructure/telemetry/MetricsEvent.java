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
public class MetricsEvent {

    private String correlationId;
    private String eventType;
    private String endpoint;
    private String method;
    private int status;
    private long durationMs;
    private LocalDateTime timestamp;
    private String userId;
}
