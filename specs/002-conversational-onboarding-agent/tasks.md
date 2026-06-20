# Tasks: Conversational Onboarding Agent (Backend)

**Input**: Design documents from `/specs/002-conversational-onboarding-agent/`
**Prerequisites**: `plan.md` (required), `spec.md` (required)

---

## Phase 1: DTOs & Controller Setup

- [x] **T001** Create DTO classes: `ChatOnboardingRequest.java` and `ChatOnboardingResponse.java` according to the API Contract in `contracts/api.md`.
- [x] **T002** Implement `ChatOnboardingController.java` to expose the `POST /api/objects/chat-onboarding` endpoint.

## Phase 2: Core Application Service

- [x] **T003** Implement `ChatOnboardingService.java` to orchestrate session load from Redis, call the AI agent for message parsing, and save the updated state.
- [x] **T004** Implement DB persistence logic within `ChatOnboardingService.java` to automatically register the new `ObjectSpecies` and `Habitat` in the PostgreSQL database once both details are resolved (`completed = true`), then clear the Redis session.

## Phase 3: LangChain4j Agent & Prompting

- [x] **T005** Define `ConversationAgent.java` interface and implement `LangChain4jConversationAgent.java` utilizing LangChain4j AI Services.
- [x] **T006** Write structured prompt templates in `PromptTemplates.java` to extract: `confirmed` (boolean), `objectName` (string), and `habitatName` (string) from natural language messages.

## Phase 4: Media Persistence (Loose End)

- [x] **T007** Update `VisionService.java` and `ChatOnboardingService.java` to persist the uploaded image bytes instead of hardcoding `"media-stub-url"`. Associate the image URL to `objectPhotoUrl` when the object registration is finalized.

## Phase 5: Testing & Validation

- [x] **T008** Add unit and integration tests verifying the conversational state machine transitions and exact JSON response structures.
