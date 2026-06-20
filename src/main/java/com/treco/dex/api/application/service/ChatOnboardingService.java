package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.ChatOnboardingResponse;
import com.treco.dex.api.domain.model.*;
import com.treco.dex.api.domain.repository.EnvironmentRepository;
import com.treco.dex.api.domain.repository.HabitatRepository;
import com.treco.dex.api.domain.repository.MediaAssetRepository;
import com.treco.dex.api.domain.repository.ObjectSpeciesRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Orchestrates the conversational onboarding flow:
 * 1. Loads the active session from Redis
 * 2. Serializes the chat history and calls the ConversationAgent (LangChain4j) to parse the user's natural language message
 * 3. Updates the session state in Redis
 * 4. When both objectName and habitatName are resolved, persists to PostgreSQL and clears the session
 */
@Service
@Slf4j
public class ChatOnboardingService {

    private final ConversationStateService conversationStateService;
    private final ConversationAgent conversationAgent;
    private final ObjectSpeciesRepository objectSpeciesRepository;
    private final HabitatRepository habitatRepository;
    private final EnvironmentRepository environmentRepository;
    private final MediaAssetRepository mediaAssetRepository;

    private final Counter jsonParseErrorCounter;
    private final Counter apiErrorCounter;
    private final Counter unknownErrorCounter;

    @Value("${app.ai.chat-history-limit:10}")
    private int chatHistoryLimit;

    public ChatOnboardingService(
            ConversationStateService conversationStateService,
            ConversationAgent conversationAgent,
            ObjectSpeciesRepository objectSpeciesRepository,
            HabitatRepository habitatRepository,
            EnvironmentRepository environmentRepository,
            MediaAssetRepository mediaAssetRepository,
            MeterRegistry meterRegistry) {
        this.conversationStateService = conversationStateService;
        this.conversationAgent = conversationAgent;
        this.objectSpeciesRepository = objectSpeciesRepository;
        this.habitatRepository = habitatRepository;
        this.environmentRepository = environmentRepository;
        this.mediaAssetRepository = mediaAssetRepository;

        // T014: Fallback metrics counters
        this.jsonParseErrorCounter = Counter.builder("ai.onboarding.fallback.count")
                .tag("reason", "json_parse_error")
                .description("Count of fallbacks due to JSON parsing errors from LLM response")
                .register(meterRegistry);
        this.apiErrorCounter = Counter.builder("ai.onboarding.fallback.count")
                .tag("reason", "api_error")
                .description("Count of fallbacks due to LLM API errors (rate-limit, timeout, etc.)")
                .register(meterRegistry);
        this.unknownErrorCounter = Counter.builder("ai.onboarding.fallback.count")
                .tag("reason", "unknown")
                .description("Count of fallbacks due to unexpected errors")
                .register(meterRegistry);
    }

    @Transactional
    public ChatOnboardingResponse processMessage(String sessionId, String userMessage, UUID userId) {
        log.info("[{}] Processing chat onboarding message. Session: {}", userId, sessionId);

        // 1. Load session from Redis
        ConversationSession session = conversationStateService.getSession(sessionId);
        if (session == null) {
            log.warn("[{}] Session not found: {}", userId, sessionId);
            throw new IllegalStateException("Sessão expirada ou não encontrada.");
        }

        // Record user message in chat history
        session.addUserMessage(userMessage);

        // T011/T012: Serialize chat history with configurable limit
        String chatHistory = serializeChatHistory(session.getChatHistory());

        // 2. Call AI agent to parse the message (now with chat history context)
        OnboardingIntent intent;
        try {
            intent = conversationAgent.parseUserMessage(
                    userMessage,
                    session.getObjectName(),
                    session.getStep(),
                    chatHistory
            );
            log.info("[{}] AI parsed intent: confirmed={}, objectName='{}', habitatName='{}'",
                    userId, intent.confirmed(), intent.objectName(), intent.habitatName());
        } catch (RuntimeException e) {
            // T013: Catch remaining RuntimeExceptions (includes LangChain4j deserialization errors)
            String exceptionName = e.getClass().getSimpleName();
            if (exceptionName.contains("Json") || exceptionName.contains("Parse")
                    || exceptionName.contains("Deserializ")) {
                log.warn("[{}] LLM response deserialization failed ({}): {}", userId, exceptionName, e.getMessage());
                jsonParseErrorCounter.increment();
            } else if (exceptionName.contains("Http") || exceptionName.contains("Timeout")
                    || exceptionName.contains("Connect")) {
                log.error("[{}] LLM API communication error ({}): {}", userId, exceptionName, e.getMessage());
                apiErrorCounter.increment();
            } else {
                log.error("[{}] Unexpected error during AI parsing ({}): {}", userId, exceptionName, e.getMessage());
                unknownErrorCounter.increment();
            }
            return buildFallbackResponse(sessionId, session);
        } catch (Exception e) {
            // T013: Final catch-all for checked exceptions
            log.error("[{}] Unexpected checked exception during AI parsing: {}", userId, e.getMessage());
            unknownErrorCounter.increment();
            return buildFallbackResponse(sessionId, session);
        }

        // 3. Update session based on parsed intent
        String resolvedObjectName = session.getObjectName();
        String resolvedHabitatName = session.getSuggestedHabitatName();

        // Update object name from intent
        if (intent.objectName() != null && !intent.objectName().isBlank()) {
            resolvedObjectName = intent.objectName().trim();
            session.setObjectName(resolvedObjectName);
        }

        // Handle confirmation/rejection
        if (intent.confirmed() != null && !intent.confirmed()) {
            // User rejected the suggested name - update with their correction
            if (intent.objectName() != null && !intent.objectName().isBlank()) {
                resolvedObjectName = intent.objectName().trim();
                session.setObjectName(resolvedObjectName);
            }
        }

        // Update habitat name from intent
        if (intent.habitatName() != null && !intent.habitatName().isBlank()) {
            resolvedHabitatName = intent.habitatName().trim();
            session.setSuggestedHabitatName(resolvedHabitatName);
        }

        // Determine the new step
        String newStep;
        boolean completed = false;

        boolean hasObjectName = resolvedObjectName != null && !resolvedObjectName.isBlank();
        boolean hasHabitat = resolvedHabitatName != null && !resolvedHabitatName.isBlank();

        if (hasObjectName && hasHabitat) {
            newStep = "COMPLETED";
            completed = true;
        } else if (hasObjectName) {
            newStep = "AWAITING_HABITAT";
        } else {
            newStep = "AWAITING_PHOTO_CONFIRMATION";
        }

        session.setStep(newStep);

        // Generate reply
        String reply = intent.reply();
        if (reply == null || reply.isBlank()) {
            if (completed) {
                reply = "Perfeito! O treco \"" + resolvedObjectName + "\" foi registrado no habitat \"" + resolvedHabitatName + "\"!";
            } else if (!hasHabitat) {
                reply = "Entendido! O treco é um(a) \"" + resolvedObjectName + "\". Agora me diga: onde você deseja guardar este treco?";
            } else {
                reply = "Desculpe, não entendi. Poderia me dizer o nome do objeto e onde deseja guardá-lo?";
            }
        }

        session.addAiMessage(reply);

        // 4. If completed, persist to database and clear Redis session
        if (completed) {
            try {
                persistOnboardingResult(resolvedObjectName, resolvedHabitatName, session.getObjectPhotoUrl(), userId);
                log.info("[{}] Onboarding completed and persisted. Object='{}', Habitat='{}'",
                        userId, resolvedObjectName, resolvedHabitatName);
            } catch (Exception e) {
                log.error("[{}] Failed to persist onboarding result: {}", userId, e.getMessage());
                // Don't fail the response - the data is still in the session
                reply += "\n\n⚠️ Nota: houve um problema ao salvar no banco de dados. Seus dados estão seguros na sessão.";
                completed = false;
                session.setStep("AWAITING_HABITAT");
            }

            // Clear session from Redis on successful persist
            if (completed) {
                conversationStateService.deleteSession(sessionId);
            } else {
                conversationStateService.saveSession(sessionId, session);
            }
        } else {
            conversationStateService.saveSession(sessionId, session);
        }

        return ChatOnboardingResponse.builder()
                .sessionId(sessionId)
                .step(newStep)
                .objectName(resolvedObjectName)
                .habitatName(resolvedHabitatName)
                .reply(reply)
                .completed(completed)
                .build();
    }

    /**
     * T011/T012: Serializes the chat history from the session into a formatted string
     * for inclusion in the LLM prompt. Limits to the last N messages as configured
     * by app.ai.chat-history-limit to avoid context window overflow.
     */
    String serializeChatHistory(List<ConversationSession.ChatHistoryEntry> chatHistory) {
        if (chatHistory == null || chatHistory.isEmpty()) {
            return "(nenhuma mensagem anterior)";
        }

        List<ConversationSession.ChatHistoryEntry> limitedHistory;
        if (chatHistory.size() > chatHistoryLimit) {
            limitedHistory = chatHistory.subList(chatHistory.size() - chatHistoryLimit, chatHistory.size());
        } else {
            limitedHistory = chatHistory;
        }

        return limitedHistory.stream()
                .map(entry -> entry.getSender() + ": " + entry.getMessage())
                .collect(Collectors.joining("\n"));
    }

    /**
     * T013: Builds a standardized fallback response when the AI agent fails to parse
     * the user's message. The session is saved so the user can retry.
     */
    private ChatOnboardingResponse buildFallbackResponse(String sessionId, ConversationSession session) {
        String fallbackReply = "Desculpe, não consegui processar sua mensagem. " +
                "Poderia reformular? Por exemplo: 'Sim, é um " + session.getObjectName() +
                " e fica na mesa do escritório'";
        session.addAiMessage(fallbackReply);
        conversationStateService.saveSession(sessionId, session);

        return ChatOnboardingResponse.builder()
                .sessionId(sessionId)
                .step(session.getStep())
                .objectName(session.getObjectName())
                .habitatName(session.getSuggestedHabitatName())
                .reply(fallbackReply)
                .completed(false)
                .build();
    }

    /**
     * Persists the resolved object and habitat to the PostgreSQL database.
     * Creates the habitat if it doesn't exist, then creates the object species.
     */
    private void persistOnboardingResult(String objectName, String habitatName, String photoUrl, UUID userId) {
        // Find or create a default environment for this user
        Environment environment = findOrCreateDefaultEnvironment(userId);

        // Find or create the habitat
        Habitat habitat = habitatRepository.findByOwnerIdAndName(userId, habitatName)
                .orElseGet(() -> {
                    Habitat newHabitat = Habitat.builder()
                            .owner(UserAccount.builder().id(userId).build())
                            .name(habitatName)
                            .description("Habitat criado via onboarding conversacional")
                            .isPrimary(true)
                            .isRefuge(false)
                            .environment(environment)
                            .build();
                    Habitat saved = habitatRepository.save(newHabitat);
                    log.info("[{}] Created new habitat '{}' with id {}", userId, habitatName, saved.getId());
                    return saved;
                });

        // Check if object already exists
        Optional<ObjectSpecies> existingObject = objectSpeciesRepository.findByOwnerIdAndName(userId, objectName);
        ObjectSpecies saved;
        if (existingObject.isPresent()) {
            log.info("[{}] Object '{}' already exists. Skipping creation.", userId, objectName);
            saved = existingObject.get();
        } else {
            // Create the object species
            ObjectSpecies objectSpecies = ObjectSpecies.builder()
                    .owner(UserAccount.builder().id(userId).build())
                    .name(objectName)
                    .description("Treco cadastrado via onboarding conversacional do TrecoDex")
                    .primaryHabitat(habitat)
                    .environment(environment)
                    .build();

            saved = objectSpeciesRepository.save(objectSpecies);
            log.info("[{}] Created new object species '{}' with id {}", userId, objectName, saved.getId());
        }

        // Persist photo as a MediaAsset if present
        if (photoUrl != null && !photoUrl.isBlank()) {
            List<MediaAsset> existingAssets = mediaAssetRepository.findByObjectSpeciesId(saved.getId());
            boolean alreadyExists = existingAssets.stream()
                    .anyMatch(asset -> photoUrl.equals(asset.getUrl()));

            if (!alreadyExists) {
                MediaAsset mediaAsset = MediaAsset.builder()
                        .objectSpecies(saved)
                        .uploadedBy(UserAccount.builder().id(userId).build())
                        .url(photoUrl)
                        .mediaType("image/png")
                        .build();
                mediaAssetRepository.save(mediaAsset);
                log.info("[{}] Associated photo to object species '{}'", userId, objectName);
            }
        }
    }

    /**
     * Finds or creates a default "Casa" environment for the user.
     */
    private Environment findOrCreateDefaultEnvironment(UUID userId) {
        List<Environment> environments = environmentRepository.findByOwnerId(userId);
        if (!environments.isEmpty()) {
            return environments.get(0); // Use the first existing environment
        }

        // Create a default environment
        Environment defaultEnv = Environment.builder()
                .owner(UserAccount.builder().id(userId).build())
                .name("Casa")
                .description("Ambiente padrão criado via onboarding conversacional")
                .build();

        Environment saved = environmentRepository.save(defaultEnv);
        log.info("[{}] Created default environment 'Casa' with id {}", userId, saved.getId());
        return saved;
    }
}
