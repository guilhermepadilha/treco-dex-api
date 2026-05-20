package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.VisualSearchResponse;
import com.treco.dex.api.domain.model.Habitat;
import com.treco.dex.api.domain.model.ObjectSpecies;
import com.treco.dex.api.domain.repository.ObjectSpeciesRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VisionServiceTest {

    @Mock
    private ChatLanguageModel chatLanguageModel;

    @Mock
    private ObjectSpeciesRepository objectSpeciesRepository;

    @Mock
    private ConversationStateService conversationStateService;

    @Mock
    private RecommendationService recommendationService;

    private VisionService visionService;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        visionService = new VisionService(
                chatLanguageModel,
                objectSpeciesRepository,
                conversationStateService,
                recommendationService
        );
        ReflectionTestUtils.setField(visionService, "apiKey", "demo");
    }

    @Test
    void testSearchByImage_FactualMatch() {
        // Given
        byte[] mockBytes = new byte[]{1, 2, 3};
        Habitat habitat = Habitat.builder().name("Segunda Gaveta").build();
        ObjectSpecies species = ObjectSpecies.builder()
                .name("escorredor de massa")
                .primaryHabitat(habitat)
                .build();

        when(objectSpeciesRepository.findByOwnerIdAndName(userId, "escorredor de massa"))
                .thenReturn(Optional.of(species));

        // When
        VisualSearchResponse response = visionService.searchByImage(mockBytes, userId);

        // Then
        assertTrue(response.isIdentified());
        assertEquals("escorredor de massa", response.getObjectName());
        assertEquals("Segunda Gaveta", response.getHabitatName());
        verifyNoInteractions(conversationStateService);
    }

    @Test
    void testSearchByImage_NoFactualMatch_InitializesRedisState() {
        // Given
        byte[] mockBytes = new byte[]{1, 2, 3};
        when(objectSpeciesRepository.findByOwnerIdAndName(userId, "escorredor de massa"))
                .thenReturn(Optional.empty());

        // When
        VisualSearchResponse response = visionService.searchByImage(mockBytes, userId);

        // Then
        assertFalse(response.isIdentified());
        assertEquals("escorredor de massa", response.getObjectName());
        assertEquals(userId.toString(), response.getSessionId());
        assertTrue(response.getMessage().contains("Isso é um escorredor de massa?"));
        
        verify(conversationStateService, times(1)).saveSession(eq(userId.toString()), any(ConversationSession.class));
    }
}
