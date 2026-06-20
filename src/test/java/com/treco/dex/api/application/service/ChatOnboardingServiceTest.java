package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.ChatOnboardingResponse;
import com.treco.dex.api.domain.model.OnboardingIntent;
import com.treco.dex.api.domain.model.Environment;
import com.treco.dex.api.domain.model.Habitat;
import com.treco.dex.api.domain.model.MediaAsset;
import com.treco.dex.api.domain.model.ObjectSpecies;
import com.treco.dex.api.domain.repository.EnvironmentRepository;
import com.treco.dex.api.domain.repository.HabitatRepository;
import com.treco.dex.api.domain.repository.MediaAssetRepository;
import com.treco.dex.api.domain.repository.ObjectSpeciesRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatOnboardingServiceTest {

    @Mock
    private ConversationStateService conversationStateService;

    @Mock
    private ConversationAgent conversationAgent;

    @Mock
    private ObjectSpeciesRepository objectSpeciesRepository;

    @Mock
    private HabitatRepository habitatRepository;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Mock
    private MediaAssetRepository mediaAssetRepository;

    private MeterRegistry meterRegistry;
    private ChatOnboardingService chatOnboardingService;
    private final UUID userId = UUID.randomUUID();
    private final String sessionId = userId.toString();

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        chatOnboardingService = new ChatOnboardingService(
                conversationStateService,
                conversationAgent,
                objectSpeciesRepository,
                habitatRepository,
                environmentRepository,
                mediaAssetRepository,
                meterRegistry
        );
        // Set default chat history limit for tests
        ReflectionTestUtils.setField(chatOnboardingService, "chatHistoryLimit", 10);
    }

    // =========================================================================
    // Existing tests (updated for new ConversationAgent signature with chatHistory)
    // =========================================================================

    @Test
    void testProcessMessageUserConfirmsAndProvidesHabitatCompletesOnboarding() {
        // Given: Active session with suggested object "mouse"
        ConversationSession session = ConversationSession.builder()
                .userId(userId.toString())
                .step("AWAITING_PHOTO_CONFIRMATION")
                .objectName("mouse")
                .objectPhotoUrl("data:image/png;base64,abc123")
                .build();

        when(conversationStateService.getSession(sessionId)).thenReturn(session);

        // AI parses: confirmed=true, objectName="mouse", habitatName="mesa do escritório"
        OnboardingIntent intent = new OnboardingIntent(
                true,
                "mouse",
                "mesa do escritório",
                "Entendido! O seu mouse será guardado na mesa do escritório."
        );
        when(conversationAgent.parseUserMessage(
                eq("Sim é um mouse e ele vive na mesa do escritório"),
                eq("mouse"),
                eq("AWAITING_PHOTO_CONFIRMATION"),
                anyString()
        )).thenReturn(intent);

        // Mock DB persistence
        Environment env = Environment.builder().id(UUID.randomUUID()).name("Casa").build();
        when(environmentRepository.findByOwnerId(userId)).thenReturn(List.of(env));
        when(habitatRepository.findByOwnerIdAndName(eq(userId), eq("mesa do escritório")))
                .thenReturn(Optional.empty());
        when(habitatRepository.save(any(Habitat.class)))
                .thenAnswer(inv -> {
                    Habitat h = inv.getArgument(0);
                    h.setId(UUID.randomUUID());
                    return h;
                });
        when(objectSpeciesRepository.findByOwnerIdAndName(userId, "mouse"))
                .thenReturn(Optional.empty());
        when(objectSpeciesRepository.save(any(ObjectSpecies.class)))
                .thenAnswer(inv -> {
                    ObjectSpecies o = inv.getArgument(0);
                    o.setId(UUID.randomUUID());
                    return o;
                });
        when(mediaAssetRepository.findByObjectSpeciesId(any(UUID.class))).thenReturn(List.of());
        when(mediaAssetRepository.save(any(MediaAsset.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ChatOnboardingResponse response = chatOnboardingService.processMessage(
                sessionId,
                "Sim é um mouse e ele vive na mesa do escritório",
                userId
        );

        // Then
        assertTrue(response.isCompleted());
        assertEquals("COMPLETED", response.getStep());
        assertEquals("mouse", response.getObjectName());
        assertEquals("mesa do escritório", response.getHabitatName());
        assertNotNull(response.getReply());
        assertEquals(sessionId, response.getSessionId());

        // Verify DB persistence
        verify(habitatRepository, times(1)).save(any(Habitat.class));
        verify(objectSpeciesRepository, times(1)).save(any(ObjectSpecies.class));
        verify(mediaAssetRepository, times(1)).save(any(MediaAsset.class));

        // Verify Redis session was deleted on completion
        verify(conversationStateService, times(1)).deleteSession(sessionId);
    }

    @Test
    void testProcessMessageUserRejectsObjectWaitsForHabitat() {
        // Given: Active session with suggested object "caneca"
        ConversationSession session = ConversationSession.builder()
                .userId(userId.toString())
                .step("AWAITING_PHOTO_CONFIRMATION")
                .objectName("caneca")
                .objectPhotoUrl("data:image/png;base64,abc123")
                .build();

        when(conversationStateService.getSession(sessionId)).thenReturn(session);

        // AI parses: confirmed=false, objectName="copo de vidro", habitatName=null
        OnboardingIntent intent = new OnboardingIntent(
                false,
                "copo de vidro",
                null,
                "Entendido, é um copo de vidro! Agora me diga, onde você deseja guardar este treco?"
        );
        when(conversationAgent.parseUserMessage(
                eq("Não, é um copo de vidro"),
                eq("caneca"),
                eq("AWAITING_PHOTO_CONFIRMATION"),
                anyString()
        )).thenReturn(intent);

        // When
        ChatOnboardingResponse response = chatOnboardingService.processMessage(
                sessionId,
                "Não, é um copo de vidro",
                userId
        );

        // Then
        assertFalse(response.isCompleted());
        assertEquals("AWAITING_HABITAT", response.getStep());
        assertEquals("copo de vidro", response.getObjectName());
        assertNull(response.getHabitatName());

        // Verify session was saved (not deleted)
        verify(conversationStateService, times(1)).saveSession(eq(sessionId), any(ConversationSession.class));
        verify(conversationStateService, never()).deleteSession(anyString());
    }

    @Test
    void testProcessMessageSessionNotFoundThrowsException() {
        // Given: No active session
        when(conversationStateService.getSession(sessionId)).thenReturn(null);

        // When/Then
        assertThrows(IllegalStateException.class, () -> {
            chatOnboardingService.processMessage(sessionId, "qualquer mensagem", userId);
        });
    }

    @Test
    void testProcessMessageAiParseFailsReturnsFallbackReply() {
        // Given
        ConversationSession session = ConversationSession.builder()
                .userId(userId.toString())
                .step("AWAITING_PHOTO_CONFIRMATION")
                .objectName("mouse")
                .build();

        when(conversationStateService.getSession(sessionId)).thenReturn(session);
        when(conversationAgent.parseUserMessage(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("AI service unavailable"));

        // When
        ChatOnboardingResponse response = chatOnboardingService.processMessage(
                sessionId,
                "teste",
                userId
        );

        // Then
        assertFalse(response.isCompleted());
        assertTrue(response.getReply().contains("Desculpe"));
        verify(conversationStateService, times(1)).saveSession(eq(sessionId), any(ConversationSession.class));

        // T014: Verify metric was incremented
        double unknownCount = meterRegistry.counter("ai.onboarding.fallback.count", "reason", "unknown").count();
        assertEquals(1.0, unknownCount);
    }

    @Test
    void testProcessMessageUserProvidesBothFieldsAtOnceFullNlpExtraction() {
        // Given: Session with suggested object "quebra-cabeça"
        ConversationSession session = ConversationSession.builder()
                .userId(userId.toString())
                .step("AWAITING_PHOTO_CONFIRMATION")
                .objectName("quebra-cabeça")
                .objectPhotoUrl("data:image/png;base64,xyz")
                .build();

        when(conversationStateService.getSession(sessionId)).thenReturn(session);

        // AI extracts BOTH fields from a single natural language message
        OnboardingIntent intent = new OnboardingIntent(
                false,
                "Cubo mágico",
                "mesa do escritório",
                "Perfeito! O Cubo mágico foi registrado na mesa do escritório!"
        );
        when(conversationAgent.parseUserMessage(
                eq("Cubo mágico, e vive em cima da mesa do escritório"),
                eq("quebra-cabeça"),
                eq("AWAITING_PHOTO_CONFIRMATION"),
                anyString()
        )).thenReturn(intent);

        // Mock DB persistence
        Environment env = Environment.builder().id(UUID.randomUUID()).name("Casa").build();
        when(environmentRepository.findByOwnerId(userId)).thenReturn(List.of(env));
        when(habitatRepository.findByOwnerIdAndName(eq(userId), eq("mesa do escritório")))
                .thenReturn(Optional.empty());
        when(habitatRepository.save(any(Habitat.class)))
                .thenAnswer(inv -> { Habitat h = inv.getArgument(0); h.setId(UUID.randomUUID()); return h; });
        when(objectSpeciesRepository.findByOwnerIdAndName(userId, "Cubo mágico"))
                .thenReturn(Optional.empty());
        when(objectSpeciesRepository.save(any(ObjectSpecies.class)))
                .thenAnswer(inv -> { ObjectSpecies o = inv.getArgument(0); o.setId(UUID.randomUUID()); return o; });
        when(mediaAssetRepository.findByObjectSpeciesId(any(UUID.class))).thenReturn(List.of());
        when(mediaAssetRepository.save(any(MediaAsset.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ChatOnboardingResponse response = chatOnboardingService.processMessage(
                sessionId,
                "Cubo mágico, e vive em cima da mesa do escritório",
                userId
        );

        // Then: Both fields extracted in one shot → COMPLETED
        assertTrue(response.isCompleted());
        assertEquals("COMPLETED", response.getStep());
        assertEquals("Cubo mágico", response.getObjectName());
        assertEquals("mesa do escritório", response.getHabitatName());

        verify(conversationStateService, times(1)).deleteSession(sessionId);
        verify(mediaAssetRepository, times(1)).save(any(MediaAsset.class));
    }

    // =========================================================================
    // T015: New tests for JSON Mode, chat history, and error differentiation
    // =========================================================================

    @Test
    void testProcessMessageValidIntentDoesNotTriggerFallback() {
        // T015.1: When the mock LLM returns a valid OnboardingIntent, fallback is NOT triggered
        ConversationSession session = ConversationSession.builder()
                .userId(userId.toString())
                .step("AWAITING_PHOTO_CONFIRMATION")
                .objectName("grampeador")
                .build();

        when(conversationStateService.getSession(sessionId)).thenReturn(session);

        OnboardingIntent intent = new OnboardingIntent(
                true,
                "grampeador",
                null,
                "Beleza! Grampeador confirmado. Agora me diga: onde ele vive?"
        );
        when(conversationAgent.parseUserMessage(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(intent);

        // When
        ChatOnboardingResponse response = chatOnboardingService.processMessage(
                sessionId,
                "Sim, é um grampeador",
                userId
        );

        // Then: No fallback, proper response
        assertFalse(response.getReply().contains("Desculpe"));
        assertEquals("grampeador", response.getObjectName());
        assertEquals("AWAITING_HABITAT", response.getStep());

        // Verify no fallback metrics were incremented
        double jsonErrors = meterRegistry.counter("ai.onboarding.fallback.count", "reason", "json_parse_error").count();
        double apiErrors = meterRegistry.counter("ai.onboarding.fallback.count", "reason", "api_error").count();
        double unknownErrors = meterRegistry.counter("ai.onboarding.fallback.count", "reason", "unknown").count();
        assertEquals(0.0, jsonErrors);
        assertEquals(0.0, apiErrors);
        assertEquals(0.0, unknownErrors);
    }

    @Test
    void testProcessMessageJsonParseExceptionTriggersFallbackWithJsonMetric() {
        // T015.2: When the mock LLM throws a JSON-related exception, fallback triggers with WARN log
        ConversationSession session = ConversationSession.builder()
                .userId(userId.toString())
                .step("AWAITING_PHOTO_CONFIRMATION")
                .objectName("mouse")
                .build();

        when(conversationStateService.getSession(sessionId)).thenReturn(session);

        // Use a custom RuntimeException subclass whose name contains "Json"
        when(conversationAgent.parseUserMessage(anyString(), anyString(), anyString(), anyString()))
                .thenAnswer(inv -> {
                    throw new JsonParsingRuntimeException("Unexpected character");
                });

        // When
        ChatOnboardingResponse response = chatOnboardingService.processMessage(
                sessionId,
                "teste json error",
                userId
        );

        // Then
        assertFalse(response.isCompleted());
        assertTrue(response.getReply().contains("Desculpe"));

        // T014: Verify json_parse_error metric incremented
        double jsonErrors = meterRegistry.counter("ai.onboarding.fallback.count", "reason", "json_parse_error").count();
        assertEquals(1.0, jsonErrors);
    }

    @Test
    void testProcessMessageHttpExceptionTriggersFallbackWithApiMetric() {
        // T015.2 (variant): When the LLM throws an HTTP-related RuntimeException, api_error metric incremented
        ConversationSession session = ConversationSession.builder()
                .userId(userId.toString())
                .step("AWAITING_PHOTO_CONFIRMATION")
                .objectName("mouse")
                .build();

        when(conversationStateService.getSession(sessionId)).thenReturn(session);

        // Simulate an HTTP-related RuntimeException — the catch block inspects the class simple name
        // We create a custom exception whose class name contains "Http" to trigger the api_error branch
        when(conversationAgent.parseUserMessage(anyString(), anyString(), anyString(), anyString()))
                .thenAnswer(inv -> {
                    throw new HttpClientException("429 Too Many Requests");
                });

        // When
        ChatOnboardingResponse response = chatOnboardingService.processMessage(
                sessionId,
                "teste http error",
                userId
        );

        // Then
        assertFalse(response.isCompleted());
        assertTrue(response.getReply().contains("Desculpe"));

        // Verify api_error metric was incremented (HttpTimeoutException contains "Http" and "Timeout")
        double apiErrors = meterRegistry.counter("ai.onboarding.fallback.count", "reason", "api_error").count();
        assertEquals(1.0, apiErrors);
    }

    @Test
    void testSerializeChatHistoryLimitsToConfiguredMaximum() {
        // T015.3: When session has more than chatHistoryLimit messages, only the last N are sent
        ReflectionTestUtils.setField(chatOnboardingService, "chatHistoryLimit", 3);

        List<ConversationSession.ChatHistoryEntry> history = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            history.add(ConversationSession.ChatHistoryEntry.builder()
                    .sender(i % 2 == 0 ? "AI" : "USER")
                    .message("Message " + i)
                    .timestamp(System.currentTimeMillis())
                    .build());
        }

        String serialized = chatOnboardingService.serializeChatHistory(history);

        // Only last 3 messages should be included (messages 6, 7, 8)
        assertFalse(serialized.contains("Message 1"));
        assertFalse(serialized.contains("Message 5"));
        assertTrue(serialized.contains("Message 6"));
        assertTrue(serialized.contains("Message 7"));
        assertTrue(serialized.contains("Message 8"));
    }

    @Test
    void testSerializeChatHistoryEmptyReturnsPlaceholder() {
        // Edge case: empty history
        String serialized = chatOnboardingService.serializeChatHistory(new ArrayList<>());
        assertEquals("(nenhuma mensagem anterior)", serialized);
    }

    @Test
    void testSerializeChatHistoryNullReturnsPlaceholder() {
        // Edge case: null history
        String serialized = chatOnboardingService.serializeChatHistory(null);
        assertEquals("(nenhuma mensagem anterior)", serialized);
    }

    @Test
    void testSerializeChatHistoryFormatsCorrectly() {
        // Verify the format "SENDER: message" separated by newlines
        List<ConversationSession.ChatHistoryEntry> history = List.of(
                ConversationSession.ChatHistoryEntry.builder()
                        .sender("AI").message("Isso é um grampeador?").timestamp(1L).build(),
                ConversationSession.ChatHistoryEntry.builder()
                        .sender("USER").message("Não, é um clips").timestamp(2L).build()
        );

        String serialized = chatOnboardingService.serializeChatHistory(history);
        assertEquals("AI: Isso é um grampeador?\nUSER: Não, é um clips", serialized);
    }

    // =========================================================================
    // T016: End-to-end test simulating full conversation with history
    // =========================================================================

    @Test
    void testEndToEndConversationWithHistoryContextPreservation() {
        // T016: Simulate a full conversation where the user corrects the AI after a suggestion.
        // Session already has 3 messages from previous interactions.
        List<ConversationSession.ChatHistoryEntry> existingHistory = new ArrayList<>();
        existingHistory.add(ConversationSession.ChatHistoryEntry.builder()
                .sender("AI")
                .message("Não encontrei esse ser no meu catálogo. Isso é um grampeador?")
                .timestamp(1000L)
                .build());
        existingHistory.add(ConversationSession.ChatHistoryEntry.builder()
                .sender("USER")
                .message("É um clips de papel e vive dentro do estojo")
                .timestamp(2000L)
                .build());
        existingHistory.add(ConversationSession.ChatHistoryEntry.builder()
                .sender("AI")
                .message("Entendido! Então é um clips de papel. Onde ele vive?")
                .timestamp(3000L)
                .build());

        ConversationSession session = ConversationSession.builder()
                .userId(userId.toString())
                .step("AWAITING_PHOTO_CONFIRMATION")
                .objectName("grampeador")
                .objectPhotoUrl("data:image/png;base64,abc123")
                .chatHistory(existingHistory)
                .build();

        when(conversationStateService.getSession(sessionId)).thenReturn(session);

        // The AI should understand from history that user said "clips de papel" and "estojo"
        OnboardingIntent intent = new OnboardingIntent(
                false,
                "clips de papel",
                "estojo",
                "Registrado! O clips de papel foi guardado no estojo. Um treco organizado é um treco feliz! 📎"
        );

        // The key assertion: the agent receives chat history as the 4th parameter
        when(conversationAgent.parseUserMessage(
                eq("Não, é um clips de papel e fica dentro do meu estojo"),
                eq("grampeador"),
                eq("AWAITING_PHOTO_CONFIRMATION"),
                argThat(history -> history.contains("AI: Não encontrei esse ser")
                        && history.contains("USER: É um clips de papel"))
        )).thenReturn(intent);

        // Mock DB persistence
        Environment env = Environment.builder().id(UUID.randomUUID()).name("Casa").build();
        when(environmentRepository.findByOwnerId(userId)).thenReturn(List.of(env));
        when(habitatRepository.findByOwnerIdAndName(eq(userId), eq("estojo")))
                .thenReturn(Optional.empty());
        when(habitatRepository.save(any(Habitat.class)))
                .thenAnswer(inv -> { Habitat h = inv.getArgument(0); h.setId(UUID.randomUUID()); return h; });
        when(objectSpeciesRepository.findByOwnerIdAndName(userId, "clips de papel"))
                .thenReturn(Optional.empty());
        when(objectSpeciesRepository.save(any(ObjectSpecies.class)))
                .thenAnswer(inv -> { ObjectSpecies o = inv.getArgument(0); o.setId(UUID.randomUUID()); return o; });
        when(mediaAssetRepository.findByObjectSpeciesId(any(UUID.class))).thenReturn(List.of());
        when(mediaAssetRepository.save(any(MediaAsset.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ChatOnboardingResponse response = chatOnboardingService.processMessage(
                sessionId,
                "Não, é um clips de papel e fica dentro do meu estojo",
                userId
        );

        // Then: Both fields resolved → COMPLETED
        assertTrue(response.isCompleted());
        assertEquals("COMPLETED", response.getStep());
        assertEquals("clips de papel", response.getObjectName());
        assertEquals("estojo", response.getHabitatName());

        // Verify the AI received the chat history (4-arg call was matched)
        verify(conversationAgent).parseUserMessage(
                anyString(),
                anyString(),
                anyString(),
                argThat(history -> history.contains("AI: Não encontrei esse ser")
                        && history.contains("USER: É um clips de papel"))
        );

        // Verify persistence
        verify(conversationStateService, times(1)).deleteSession(sessionId);
        verify(objectSpeciesRepository, times(1)).save(any(ObjectSpecies.class));

        // Verify no fallback metrics
        double totalFallbacks = meterRegistry.counter("ai.onboarding.fallback.count", "reason", "json_parse_error").count()
                + meterRegistry.counter("ai.onboarding.fallback.count", "reason", "api_error").count()
                + meterRegistry.counter("ai.onboarding.fallback.count", "reason", "unknown").count();
        assertEquals(0.0, totalFallbacks);
    }

    private static class HttpClientException extends RuntimeException {
        public HttpClientException(String message) {
            super(message);
        }
    }

    private static class JsonParsingRuntimeException extends RuntimeException {
        public JsonParsingRuntimeException(String message) {
            super(message);
        }
    }
}
