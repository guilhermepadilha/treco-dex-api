package com.treco.dex.api.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class MediaEventProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishMediaUploadEvent(UUID mediaAssetId, UUID objectSpeciesId, UUID userId) {
        String topic = "media-events";
        String message = String.format("MEDIA_UPLOADED;%s;%s;%s", mediaAssetId, objectSpeciesId, userId);
        
        log.info("Publishing media event: {}", message);
        kafkaTemplate.send(topic, mediaAssetId.toString(), message);
    }
}
