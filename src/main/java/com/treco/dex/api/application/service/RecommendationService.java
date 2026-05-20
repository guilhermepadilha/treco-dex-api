package com.treco.dex.api.application.service;

import com.treco.dex.api.domain.model.Habitat;
import com.treco.dex.api.domain.model.ObjectSpecies;
import com.treco.dex.api.domain.repository.HabitatRepository;
import com.treco.dex.api.domain.repository.ObjectSpeciesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendationService {

    private final ObjectSpeciesRepository objectSpeciesRepository;
    private final HabitatRepository habitatRepository;
    private final RecommendationAiClient recommendationAiClient;

    public RecommendationService(
            ObjectSpeciesRepository objectSpeciesRepository,
            HabitatRepository habitatRepository,
            RecommendationAiClient recommendationAiClient) {
        this.objectSpeciesRepository = objectSpeciesRepository;
        this.habitatRepository = habitatRepository;
        this.recommendationAiClient = recommendationAiClient;
    }

    public RecommendationResult recommend(String objectName, UUID userId) {
        log.info("[{}] Requesting habitat recommendation for object: {}", userId, objectName);

        // 1. Factual Lookup: Check if object already exists in user's catalog
        Optional<ObjectSpecies> existingObject = objectSpeciesRepository.findByOwnerIdAndName(userId, objectName);
        if (existingObject.isPresent()) {
            ObjectSpecies obj = existingObject.get();
            String habitatName = obj.getPrimaryHabitat().getName();
            log.info("[{}] Factual match found in database. Object: {}, Habitat: {}", userId, objectName, habitatName);
            return new RecommendationResult(
                    habitatName,
                    "Esse é o [" + objectName + "] ele vive na [" + habitatName + "]!"
            );
        }

        // 2. Fetch existing habitats of the user to pass as context
        List<Habitat> existingHabitats = habitatRepository.findByOwnerId(userId);
        List<String> habitatNames = existingHabitats.stream()
                .map(Habitat::getName)
                .collect(Collectors.toList());

        // 3. AI recommendation call with grace fallback
        try {
            log.info("[{}] No factual match found. Calling LLM with existing habitats: {}", userId, habitatNames);
            RecommendationResult result = recommendationAiClient.recommendHabitat(objectName, habitatNames);
            log.info("[{}] LLM recommendation succeeded: {}", userId, result);
            return result;
        } catch (Exception e) {
            log.error("[{}] LLM recommendation failed. Applying local lexical fallback.", userId, e);
            return fallbackLexicalRecommendation(objectName, habitatNames);
        }
    }

    private RecommendationResult fallbackLexicalRecommendation(String objectName, List<String> existingHabitats) {
        String lowerName = objectName.toLowerCase();
        
        // Simple rules for kitchen/talheres
        if (lowerName.contains("garfo") || lowerName.contains("faca") || lowerName.contains("colher") || lowerName.contains("prato") || lowerName.contains("copo") || lowerName.contains("panela")) {
            for (String hab : existingHabitats) {
                String lh = hab.toLowerCase();
                if (lh.contains("cozinha") || lh.contains("gaveta") || lh.contains("talher") || lh.contains("armário") || lh.contains("paneleiro")) {
                    return new RecommendationResult(hab, "Identifiquei que " + objectName + " pertence à cozinha e selecionei seu habitat '" + hab + "' localmente!");
                }
            }
            return new RecommendationResult("Armário de Cozinha", "Sugiro guardar o(a) " + objectName + " no 'Armário de Cozinha' (sugestão do sistema local).");
        }

        // Simple rules for clothes/quarto
        if (lowerName.contains("camisa") || lowerName.contains("calça") || lowerName.contains("meia") || lowerName.contains("casaco") || lowerName.contains("roupa")) {
            for (String hab : existingHabitats) {
                String lh = hab.toLowerCase();
                if (lh.contains("quarto") || lh.contains("guarda") || lh.contains("armário") || lh.contains("gaveta") || lh.contains("closet")) {
                    return new RecommendationResult(hab, "Detectei que " + objectName + " é uma vestimenta e associei ao habitat '" + hab + "' localmente!");
                }
            }
            return new RecommendationResult("Guarda-Roupa", "Recomendo guardar o(a) " + objectName + " no seu 'Guarda-Roupa' (sugestão do sistema local).");
        }

        // Default local suggestion
        if (!existingHabitats.isEmpty()) {
            String firstHab = existingHabitats.get(0);
            return new RecommendationResult(firstHab, "Não consegui falar com a IA, mas selecionei seu habitat '" + firstHab + "' por ser seu habitat mais acessível.");
        }

        return new RecommendationResult("Gaveta Principal", "Não consegui falar com a IA e você não tem habitats. Sugiro o habitat provisório 'Gaveta Principal'.");
    }
}
