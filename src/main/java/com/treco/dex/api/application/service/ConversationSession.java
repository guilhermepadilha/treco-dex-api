package com.treco.dex.api.application.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationSession implements Serializable {
    private String userId;
    private String step;
    private String objectName;
    private String objectPhotoUrl;
    private String suggestedHabitatName;
    private String suggestedReasoning;
}
