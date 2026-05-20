package com.treco.dex.api.api.config;

import com.treco.dex.api.application.service.RecommendationAiClient;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
public class AiConfig {

    @Value("${app.ai.base-url}")
    private String baseUrl;

    @Value("${app.ai.api-key}")
    private String apiKey;

    @Value("${app.ai.model-name}")
    private String modelName;

    @Value("${app.ai.http-referer}")
    private String httpReferer;

    @Value("${app.ai.x-title}")
    private String xTitle;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.3)
                .timeout(Duration.ofSeconds(20))
                .customHeaders(Map.of(
                        "HTTP-Referer", httpReferer,
                        "X-Title", xTitle
                ))
                .build();
    }

    @Bean
    public RecommendationAiClient recommendationAiClient(ChatLanguageModel chatLanguageModel) {
        return dev.langchain4j.service.AiServices.builder(RecommendationAiClient.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }
}
