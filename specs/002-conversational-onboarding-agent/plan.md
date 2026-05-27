# Implementation Plan: Conversational Onboarding Agent

**Branch**: `002-conversational-onboarding-agent` | **Date**: 2026-05-27 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/002-conversational-onboarding-agent/spec.md`

## Summary

O objetivo deste plano é estabelecer a arquitetura técnica e o design de software para a implementação do **Conversational Onboarding Agent** no backend `treco-dex-api`. Este agente de IA processará mensagens em linguagem natural enviadas pelo usuário no onboarding do scanner para (1) confirmar/rejeitar a classificação do objeto, (2) extrair o habitat de destino e (3) responder amigavelmente orientando os próximos passos.

Nossa abordagem utiliza **LangChain4j** com modelos LLM configurados, integrando com o estado de conversação armazenado em **Redis** e persistindo a espécie de objeto e habitat resultantes na base relacional **PostgreSQL** via JPA.

## Technical Context

**Language/Version**: Java 21 (Spring Boot 3.3.x)  
**Primary Dependencies**: LangChain4j (for AI orchestrations), Spring Data Redis, Spring Data JPA, Lombok  
**Storage**: Redis (active session cache), PostgreSQL (factual storage of objects/habitats)  
**Testing**: JUnit 5, Mockito, Spring Boot Test  
**Target Platform**: Linux server  
**Project Type**: REST Web Service  
**Performance Goals**: AI parsing and response < 2.5s, Redis fetch/write < 10ms  
**Constraints**: Model fallback policies (primary: `idia/nemotron-3-nano-omni-30b-a3b-reasoning:free` or chosen model; secondary fallback: `recraft/recraft-v4.1-pro-vector` / `nvidia/nemotron-nano-12b-v2-vl:free` depending on provider status).

## Constitution Check

*GATE: Passed. O plano cumpre todas as diretrizes da Constitution do Projeto.*

- **Domain-Driven Design (DDD)**: O domínio do onboarding e a lógica de sessão permanecem isolados na camada de domínio, livres de frameworks de IA.
- **Clean & Hexagonal Architecture**: A integração com LangChain4j é encapsulada em adaptadores de infraestrutura (`infrastructure/ai`), expondo uma interface limpa de serviço para o domínio.
- **AI-First Readiness**: Implementa um pipeline multimodal e conversacional robusto com fallbacks.

## Project Structure

### Documentation (this feature)

```text
specs/002-conversational-onboarding-agent/
├── spec.md              # Feature specification
├── plan.md              # This plan
└── checklists/
    └── requirements.md  # Quality checklist
```

### Source Code (repository root)

```text
src/main/java/com/treco/dex/api/
├── api/
│   ├── controller/
│   │   └── ChatOnboardingController.java  # Exposes POST /api/objects/chat-onboarding
│   └── dto/
│       ├── ChatOnboardingRequest.java
│       └── ChatOnboardingResponse.java
├── application/
│   └── service/
│       ├── ChatOnboardingService.java     # Orchestrates session load, AI parse, and save
│       └── ConversationAgent.java         # LangChain4j agent interface
├── domain/
│   ├── model/
│   │   ├── OnboardingSession.java         # Session state logic
│   │   └── OnboardingIntent.java          # Extracted intents (confirmed, objectName, habitatName)
│   └── repository/
│       └── OnboardingSessionRepository.java
└── infrastructure/
    ├── ai/
    │   ├── LangChain4jConversationAgent.java # Implements ConversationAgent
    │   └── PromptTemplates.java           # Structured templates for prompt engineering
    └── persistence/
        └── RedisOnboardingSessionRepository.java
```

**Structure Decision**: A estrutura segue rigorosamente o layout arquitetural estabelecido do projeto (`api`, `application`, `domain`, `infrastructure`), garantindo isolamento total da inteligência de IA e desacoplamento do framework de persistência.
