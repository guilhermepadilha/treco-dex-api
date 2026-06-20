package com.treco.dex.api.api.controller;

import com.treco.dex.api.api.dto.ChatOnboardingRequest;
import com.treco.dex.api.api.dto.ChatOnboardingResponse;
import com.treco.dex.api.application.service.ChatOnboardingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/objects")
@Slf4j
public class ChatOnboardingController {

    private final ChatOnboardingService chatOnboardingService;

    public ChatOnboardingController(ChatOnboardingService chatOnboardingService) {
        this.chatOnboardingService = chatOnboardingService;
    }

    @PostMapping("/chat-onboarding")
    public ResponseEntity<?> chatOnboarding(
            @Valid @RequestBody ChatOnboardingRequest request,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        log.info("[{}] Chat onboarding endpoint invoked. SessionId: {}", userId, request.getSessionId());

        try {
            ChatOnboardingResponse response = chatOnboardingService.processMessage(
                    request.getSessionId(),
                    request.getMessage(),
                    userId
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("[{}] Chat onboarding error: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        } catch (IllegalStateException e) {
            log.warn("[{}] Chat onboarding session not found: {}", userId, e.getMessage());
            return ResponseEntity.status(404).body(
                    Map.of("error", "Sessão expirada ou não encontrada. Por favor escaneie o objeto novamente.")
            );
        }
    }
}
