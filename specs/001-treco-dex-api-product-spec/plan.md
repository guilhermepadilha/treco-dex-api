# Implementation Plan: AI Habitat Recommendations & Vision (T033a / T033b)

**Branch**: `001-add-trecodex` | **Date**: 2026-05-20 | **Spec**: [spec.md](file:///home/guilherme.padilha/projetos/treco-dex-api/specs/001-treco-dex-api-product-spec/spec.md)
**Input**: Feature specification from `/specs/001-treco-dex-api-product-spec/spec.md`

## Summary

This plan outlines the architecture and execution strategy for integrating AI-driven conversational workflows and multimodal visual object discovery into the TrecoDex backend. It details the hybrid recommendation strategy (factual local database checks + external LLM inference), stateful conversational onboarding sessions persisted in Redis, and agnostically configured OpenAI/OpenRouter chat and vision clients using LangChain4j.

---

## Technical Context

**Language/Version**: Java 21 LTS  
**Primary Dependencies**: Spring Boot 3.2.3, LangChain4j 0.31.0 (`langchain4j-open-ai`), Spring Data Redis, Spring Kafka  
**Storage**: PostgreSQL (persisting core schemas), Redis (stateless transient conversational context sessions)  
**Testing**: JUnit 5, Mockito, Spring Boot Integration Tests  
**Target Platform**: Dockerized Linux Server  
**Project Type**: RESTful Web Service and Messaging Worker  
**Performance Goals**: REST API requests < 200ms, AI visual inference < 5s  
**Constraints**: Fully decoupled LLM model mapping via `application.yaml` config (allowing seamless migration to OpenRouter/free models), stateless-first container compatibility, graceful local fallbacks.

---

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Our design aligns perfectly with the permanent directives of the **TrecoDex Backend Constitution**:

1. **AI-First & Mandatory Decoupling**: Meets the directive by isolating LLM interaction behind LangChain4j declarative services (`RecommendationAiClient`) and completely parameterizing the API endpoint, model identifier, referer, and key in the configuration. This guarantees compatibility with OpenRouter's free and commercial models without changing Java code.
2. **Domain Isolation**: Keep core entities (`ObjectSpecies`, `Habitat`) pure. AI logic and temporary Redis session flows remain in the `application.service` and `infrastructure` layers.
3. **Resilience (Graceful Fallback)**: Built-in local fallback mechanisms execute static lexical comparisons if OpenAI/OpenRouter is down, ensuring high system uptime.
4. **Observability by Default**: Correlation IDs are propagated throughout the conversational state machine and external LLM calls to enable unified distributed tracing.

---

## Project Structure

### Documentation (this feature)

```text
specs/001-treco-dex-api-product-spec/
├── plan.md              # This implementation plan
├── research.md          # Completed Phase 0 architectural decisions
├── data-model.md        # Core entities and relationship modeling
├── quickstart.md        # Step-by-step local bootstrap manual
├── checklists/          # Checklists for verification
├── contracts/           # API Contract mapping
└── tasks.md             # Complete task execution tracking
```

### Source Code

```text
src/main/java/com/treco/dex/api/
├── api/
│   ├── controller/
│   │   ├── ObjectSpeciesController.java
│   │   └── ObjectDetectionController.java       # Visual Search Endpoint
│   ├── dto/
│   │   ├── ObjectSpeciesResponse.java
│   │   └── VisualSearchResponse.java
│   └── config/
│       └── AiConfig.java                        # Agnostic OpenRouter/OpenAI Beans
├── application/
│   ├── service/
│   │   ├── RecommendationService.java          # Hybrid Factual/AI Recommendations
│   │   ├── VisionService.java                  # Multimodal Object Discovery
│   │   ├── ConversationStateService.java       # Redis-based state engine
│   │   └── RecommendationAiClient.java         # Declarative LangChain4j Port
├── domain/
│   ├── model/
│   │   ├── ObjectSpecies.java
│   │   └── Habitat.java
│   └── repository/
│       ├── ObjectSpeciesRepository.java
│       └── HabitatRepository.java
└── infrastructure/
    └── persistence/
        └── RedisConversationRepository.java
```

---

## Complexity Tracking

*All constitution directives are met. No complexity justifications or bypasses are required.*

| Directives Evaluated | Status | Simpler Alternative Rejected Because |
| :--- | :--- | :--- |
| **Redis Session State** | Fully Approved | Simple local in-memory session was rejected because it violates the "Stateless-First" / horizontally scalable directive. |
| **LangChain4j Port** | Fully Approved | Hand-crafting HTTP REST client calls for OpenRouter was rejected as it increases boilerplate code and reduces maintainability. |

---

## Implementation Details & Interfaces

### 1. The Dynamic LLM Bean Definition (`AiConfig.java`)
```java
@Configuration
public class AiConfig {
    @Value("${app.ai.base-url}") private String baseUrl;
    @Value("${app.ai.api-key}") private String apiKey;
    @Value("${app.ai.model-name}") private String modelName;
    @Value("${app.ai.http-referer}") private String httpReferer;
    @Value("${app.ai.x-title}") private String xTitle;

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
}
```

### 2. Conversational State Machine (`ConversationStateService.java`)
Manages stateful progression stored in Redis cache:
*   `State: START` (Initial photo/name submission)
*   `State: AWAITING_PHOTO_CONFIRMATION` (User wants to provide photo of object species?)
*   `State: AWAITING_HABITAT_CONFIRMATION` (Prompting user to confirm semantic habitat matching suggestion)
*   `State: AWAITING_NEW_HABITAT_NAME` (User rejected suggestion, asking where object lives)
*   `State: AWAITING_HABITAT_PHOTO` (Asking if user wants to upload a photo of the new habitat)
*   `State: COMPLETED` (Finalizing persistence)

---

## Validation & Test Strategy

1. **Unit Verification**:
   - `RecommendationServiceTest`: Verify that factual matches from the DB are returned instantly without calling `ChatLanguageModel`.
   - Verify that fallback logic triggers when `ChatLanguageModel` throws exception.
2. **Integration Verification**:
   - Run a testcontainer for Redis to validate state transitions inside `ConversationStateService`.
   - Validate that `/api/objects/visual-search` coordinates visual identification, Redis state setup, and database checks.
