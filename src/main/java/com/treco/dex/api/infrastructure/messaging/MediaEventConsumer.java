package com.treco.dex.api.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MediaEventConsumer {

    @KafkaListener(topics = "media-events", groupId = "treco-dex-api-media-workers")
    public void consumeMediaEvent(String message) {
        log.info("Consuming media event: {}", message);
        
        // Parse and process event
        String[] parts = message.split(";");
        if (parts.length >= 4 && "MEDIA_UPLOADED".equals(parts[0])) {
            String mediaAssetId = parts[1];
            String objectSpeciesId = parts[2];
            String userId = parts[3];
            
            log.info("Processing media upload: mediaId={}, objectId={}, userId={}", mediaAssetId, objectSpeciesId, userId);
            // Future: Add image processing, AI analysis, etc.
        }
    }
}
