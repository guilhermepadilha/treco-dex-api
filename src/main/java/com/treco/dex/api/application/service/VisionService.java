package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.VisualSearchResponse;
import com.treco.dex.api.domain.model.ObjectSpecies;
import com.treco.dex.api.domain.repository.ObjectSpeciesRepository;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class VisionService {

    private final ChatLanguageModel chatLanguageModel;
    private final ObjectSpeciesRepository objectSpeciesRepository;
    private final ConversationStateService conversationStateService;
    private final RecommendationService recommendationService;

    @Value("${app.ai.api-key}")
    private String apiKey;
    @Value("${app.ai.model-name}")
    private String modelName;
    @Value("${app.ai.fallback-model}")
    private String fallbackModelName;
    @Value("${app.ai.base-url:https://openrouter.ai/api/v1}")
    private String baseUrl;
    @Value("${app.ai.http-referer:https://trecodex.com}")
    private String httpReferer;
    @Value("${app.ai.x-title:TrecoDex API}")
    private String xTitle;

    public VisionService(
            ChatLanguageModel chatLanguageModel,
            ObjectSpeciesRepository objectSpeciesRepository,
            ConversationStateService conversationStateService,
            RecommendationService recommendationService) {
        this.chatLanguageModel = chatLanguageModel;
        this.objectSpeciesRepository = objectSpeciesRepository;
        this.conversationStateService = conversationStateService;
        this.recommendationService = recommendationService;
    }

    @Transactional
    public VisualSearchResponse searchByImage(byte[] imageBytes, UUID userId) {
        log.info("[{}] Received image for visual search ({} bytes)", userId, imageBytes.length);

        String objectName;
        // 1. If key is "demo" or missing, use a deterministic stub for local testing / testcontainers compatibility
        if ("demo".equalsIgnoreCase(apiKey) || apiKey == null || apiKey.isEmpty()) {
            log.info("[{}] AI key is demo or empty. Running in stub mode.", userId);
            objectName = "escorredor de massa [demo]"; // marker indicating demo mode
        } else {
            try {
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                UserMessage userMessage = UserMessage.from(
                        TextContent.from("Identify the primary domestic object in this image. Respond ONLY with the single clean object name in Portuguese (e.g. 'escorredor de massa', 'caneca', 'controle remoto', 'garrafa')."),
                        ImageContent.from(base64Image, "image/png")
                );
                log.info("[{}] Sending multimodal request to LLM (model: {} )...", userId, modelName);
                Response<AiMessage> response = chatLanguageModel.generate(userMessage);
                objectName = response.content().text().trim().toLowerCase();
                log.info("[{}] LLM identified object: '{}' using model {}", userId, objectName, modelName);
            } catch (Exception primaryEx) {
                log.warn("[{}] Primary model {} failed: {}", userId, modelName, primaryEx.getMessage());
                if (fallbackModelName != null && !fallbackModelName.isBlank()) {
                    log.info("[{}] Trying fallback model {}...", userId, fallbackModelName);
                    try {
                        dev.langchain4j.model.openai.OpenAiChatModel fallbackModel = dev.langchain4j.model.openai.OpenAiChatModel.builder()
                                .apiKey(apiKey)
                                .baseUrl(baseUrl)
                                .modelName(fallbackModelName)
                                .temperature(0.3)
                                .timeout(java.time.Duration.ofSeconds(20))
                                .customHeaders(java.util.Map.of(
                                        "HTTP-Referer", httpReferer,
                                        "X-Title", xTitle
                                ))
                                .build();
                        
                        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                        UserMessage userMessage = UserMessage.from(
                                TextContent.from("Identify the primary domestic object in this image. Respond ONLY with the single clean object name in Portuguese (e.g. 'escorredor de massa', 'caneca', 'controle remoto', 'garrafa')."),
                                ImageContent.from(base64Image, "image/png")
                        );
                        Response<AiMessage> response = fallbackModel.generate(userMessage);
                        objectName = response.content().text().trim().toLowerCase();
                        log.info("[{}] Fallback model {} successfully identified object: '{}'", userId, fallbackModelName, objectName);
                    } catch (Exception fallbackEx) {
                        log.error("[{}] Fallback model {} failed too: {}", userId, fallbackModelName, fallbackEx.getMessage());
                        objectName = "escorredor de massa [demo]";
                    }
                } else {
                    objectName = "escorredor de massa [demo]";
                }
            }
        }

        // 2. Factual check in the local database
        Optional<ObjectSpecies> existingObject = objectSpeciesRepository.findByOwnerIdAndName(userId, objectName);
        if (existingObject.isPresent()) {
            ObjectSpecies obj = existingObject.get();
            String habitatName = obj.getPrimaryHabitat().getName();
            log.info("[{}] Factual match found for visual search: '{}' is in '{}'", userId, objectName, habitatName);
            return VisualSearchResponse.builder()
                    .identified(true)
                    .objectName(objectName)
                    .habitatName(habitatName)
                    .reasoning("Este é o [" + objectName + "]! Ele vive quentinho e seguro na [" + habitatName + "]. Embora sinta saudades do espaguete de domingo, ele adora a vibração da centrífuga! [demo]")
                    .build();
        }

        // 3. Object NOT found: initialize Redis-based onboarding state machine
        log.info("[{}] Object '{}' not found in database. Initializing Redis session.", userId, objectName);
        
        ConversationSession session = ConversationSession.builder()
                .userId(userId.toString())
                .step("AWAITING_PHOTO_CONFIRMATION")
                .objectName(objectName)
                .objectPhotoUrl("media-stub-url")
                .build();
        
        conversationStateService.saveSession(userId.toString(), session);

        return VisualSearchResponse.builder()
                .identified(false)
                .objectName(objectName)
                .sessionId(userId.toString())
                .message("Não encontrei esse ser no meu catálogo. Isso é um " + objectName + "?")
                .build();
    }
}
