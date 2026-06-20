package com.treco.dex.api.application.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    private List<ChatHistoryEntry> chatHistory = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatHistoryEntry implements Serializable {
        private String sender; // "USER" or "AI"
        private String message;
        private long timestamp;
    }

    public void addUserMessage(String message) {
        if (chatHistory == null) chatHistory = new ArrayList<>();
        chatHistory.add(ChatHistoryEntry.builder()
                .sender("USER")
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    public void addAiMessage(String message) {
        if (chatHistory == null) chatHistory = new ArrayList<>();
        chatHistory.add(ChatHistoryEntry.builder()
                .sender("AI")
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build());
    }
}
