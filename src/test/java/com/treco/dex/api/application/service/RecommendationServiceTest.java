package com.treco.dex.api.application.service;

import com.treco.dex.api.domain.model.Habitat;
import com.treco.dex.api.domain.model.ObjectSpecies;
import com.treco.dex.api.domain.repository.HabitatRepository;
import com.treco.dex.api.domain.repository.ObjectSpeciesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private ObjectSpeciesRepository objectSpeciesRepository;

    @Mock
    private HabitatRepository habitatRepository;

    @Mock
    private RecommendationAiClient recommendationAiClient;

    private RecommendationService recommendationService;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(objectSpeciesRepository, habitatRepository, recommendationAiClient);
    }

    @Test
    void testRecommend_FactualMatch() {
        // Given
        String objectName = "escorredor de massa";
        Habitat habitat = Habitat.builder().id(UUID.randomUUID()).name("Segunda Gaveta").build();
        ObjectSpecies species = ObjectSpecies.builder()
                .name(objectName)
                .primaryHabitat(habitat)
                .build();

        when(objectSpeciesRepository.findByOwnerIdAndName(userId, objectName))
                .thenReturn(Optional.of(species));

        // When
        RecommendationResult result = recommendationService.recommend(objectName, userId);

        // Then
        assertEquals("Segunda Gaveta", result.habitatName());
        assertTrue(result.reasoning().contains("vive na [Segunda Gaveta]"));
        verifyNoInteractions(recommendationAiClient);
    }

    @Test
    void testRecommend_LLMCall() {
        // Given
        String objectName = "caneca";
        when(objectSpeciesRepository.findByOwnerIdAndName(userId, objectName))
                .thenReturn(Optional.empty());
        when(habitatRepository.findByOwnerId(userId))
                .thenReturn(Collections.emptyList());
        when(recommendationAiClient.recommendHabitat(objectName, Collections.emptyList()))
                .thenReturn(new RecommendationResult("Armário Superior", "Pokédex: Caneca viva!"));

        // When
        RecommendationResult result = recommendationService.recommend(objectName, userId);

        // Then
        assertEquals("Armário Superior", result.habitatName());
        assertEquals("Pokédex: Caneca viva!", result.reasoning());
    }

    @Test
    void testRecommend_GracefulFallback() {
        // Given
        String objectName = "faca de churrasco";
        when(objectSpeciesRepository.findByOwnerIdAndName(userId, objectName))
                .thenReturn(Optional.empty());
        when(habitatRepository.findByOwnerId(userId))
                .thenReturn(List.of(Habitat.builder().name("Gaveta de Talheres").build()));
        when(recommendationAiClient.recommendHabitat(anyString(), anyList()))
                .thenThrow(new RuntimeException("AI provider offline"));

        // When
        RecommendationResult result = recommendationService.recommend(objectName, userId);

        // Then
        assertEquals("Gaveta de Talheres", result.habitatName());
        assertTrue(result.reasoning().contains("Gaveta de Talheres"));
    }
}
