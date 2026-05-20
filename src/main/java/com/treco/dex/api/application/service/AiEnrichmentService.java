package com.treco.dex.api.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiEnrichmentService {

    public String generateInitialDescription(String objectName, String habitatName) {
        log.info("Generating AI enrichment for object: {} in habitat: {}", objectName, habitatName);
        // Simple description generation - can be extended with LangChain4j later
        return "Object '" + objectName + "' located in " + habitatName + " habitat. Awaiting additional observations for enhanced context.";
    }
}
