package com.treco.dex.api.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ConversationStateService {

    private static final String SESSION_KEY_PREFIX = "trecodex:session:";
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ConversationStateService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveSession(String userId, ConversationSession session) {
        String key = SESSION_KEY_PREFIX + userId;
        try {
            String json = objectMapper.writeValueAsString(session);
            redisTemplate.opsForValue().set(key, json, 30, TimeUnit.MINUTES);
            log.info("Saved conversation session for user {}: {}", userId, json);
        } catch (Exception e) {
            log.error("Failed to serialize conversation session for user {}", userId, e);
        }
    }

    public ConversationSession getSession(String userId) {
        String key = SESSION_KEY_PREFIX + userId;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            log.debug("No active conversation session found for user {}", userId);
            return null;
        }
        try {
            return objectMapper.readValue(json, ConversationSession.class);
        } catch (Exception e) {
            log.error("Failed to deserialize conversation session for user {}", userId, e);
            return null;
        }
    }

    public void deleteSession(String userId) {
        String key = SESSION_KEY_PREFIX + userId;
        redisTemplate.delete(key);
        log.info("Deleted conversation session for user {}", userId);
    }
}
