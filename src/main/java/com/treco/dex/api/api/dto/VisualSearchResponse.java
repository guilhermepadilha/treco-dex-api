package com.treco.dex.api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisualSearchResponse {
    private boolean identified;
    private String objectName;
    private String habitatName;
    private String reasoning;
    private String sessionId;
    private String message;
}
