package com.treco.dex.api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatOnboardingResponse {

    private String sessionId;
    private String step;
    private String objectName;
    private String habitatName;
    private String reply;
    private boolean completed;
}
