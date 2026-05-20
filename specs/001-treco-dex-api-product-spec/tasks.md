# Tasks: Treco-Dex-API Backend Platform MVP

**Input**: Design documents from `specs/001-treco-dex-api-product-spec/`  
**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/api-contract.md`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Initialize backend structure, build configuration, and local runtimes.

- [x] T001 Create backend module layout in `backend/` with `build.gradle.kts`, `settings.gradle.kts`, `src/`, and `docker/`
- [x] T002 Configure Gradle Kotlin DSL build files in `backend/build.gradle.kts` and `backend/settings.gradle.kts` with Spring Boot dependencies
- [x] T003 [P] Create package structure under `backend/src/main/java/com/treco-dex-api/` for `api/`, `application/`, `domain/`, and `infrastructure/`
- [x] T004 [P] Add `backend/src/main/resources/application.yaml` and initial migration folder `backend/src/main/resources/db/migrations/`
- [x] T005 [P] Create local container bootstrap files in `backend/docker/Dockerfile` and `backend/docker/docker-compose.yml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Implement core infrastructure required before user story work begins.

- [x] T006 Setup PostgreSQL migration framework and initial schema script in `backend/src/main/resources/db/migrations/`
- [x] T007 [P] Implement JWT authentication support in `backend/src/main/java/com/treco-dex-api/api/security/`
- [x] T007a [P] Implement UserAccountRepository, UserAccountService, registration and login endpoints in `backend/src/main/java/com/treco-dex-api/api/security/`
- [x] T008 [P] Configure environment-based application properties in `backend/src/main/resources/application.yaml`
- [x] T009 Implement base REST API routing and controller advice in `backend/src/main/java/com/treco-dex-api/api/config/` and `backend/src/main/java/com/treco-dex-api/api/error/`
- [x] T010 [P] Implement structured JSON logging and correlation IDs in `backend/src/main/java/com/treco-dex-api/infrastructure/telemetry/`
- [x] T011 Create base domain repository interfaces and core entity classes for `ObjectSpecies`, `Habitat`, `Environment`, `Observation`, `ObjectStateRecord`, `UserAccount`, and `MediaAsset` in `backend/src/main/java/com/treco-dex-api/domain/`
- [x] T012 Create test skeletons in `backend/src/test/java/com/treco-dex-api/` for unit and integration work
- [x] T048 Implement `Environment` domain entity, persistence support, and habitat-to-environment association in `backend/src/main/java/com/treco-dex-api/domain/model/` and `backend/src/main/java/com/treco-dex-api/infrastructure/persistence/`

---

## Phase 3: User Story 1 - Register and Catalog Objects (Priority: P1)

**Goal**: Enable users to register object species, persist object metadata, and manage habitats.

**Independent Test**: Create an object species with habitat and verify it can be retrieved via REST CRUD.

- [x] T013 [P] Implement `ObjectSpecies` domain entity in `backend/src/main/java/com/treco-dex-api/domain/model/ObjectSpecies.java`
- [x] T014 [P] Implement `Habitat` domain entity with optional `Environment` association in `backend/src/main/java/com/treco-dex-api/domain/model/Habitat.java`
- [x] T015 Implement `ObjectSpeciesRepository` and `HabitatRepository` in `backend/src/main/java/com/treco-dex-api/infrastructure/persistence/`
- [x] T016 Implement `ObjectSpeciesService` in `backend/src/main/java/com/treco-dex-api/application/service/ObjectSpeciesService.java`
- [x] T017 Implement `ObjectSpeciesController` CRUD endpoints in `backend/src/main/java/com/treco-dex-api/api/controller/ObjectSpeciesController.java`
- [x] T018 Implement request/response DTOs and validation in `backend/src/main/java/com/treco-dex-api/api/dto/`
- [x] T019 Add unit tests for object service and controller in `backend/src/test/java/com/treco-dex-api/application/` and `backend/src/test/java/com/treco-dex-api/api/`
- [x] T020 Add integration tests for object registration and retrieval in `backend/src/test/java/com/treco-dex-api/integration/`

---

## Phase 4: User Story 2 - Locate Objects Quickly (Priority: P1)

**Goal**: Implement contextual search so users can find objects by name, habitat, and state.

**Independent Test**: Search objects and verify responses include habitat and last observation context.

- [x] T021 [P] Implement `SearchService` in `backend/src/main/java/com/treco-dex-api/application/service/SearchService.java`
- [x] T022 Implement `SearchController` endpoint in `backend/src/main/java/com/treco-dex-api/api/controller/SearchController.java`
- [x] T023 Implement filtered search repository queries in `backend/src/main/java/com/treco-dex-api/infrastructure/persistence/SearchRepository.java`
- [x] T024 Implement contextual object summary DTOs in `backend/src/main/java/com/treco-dex-api/api/dto/`
- [x] T025 Add search integration tests in `backend/src/test/java/com/treco-dex-api/integration/`

---

## Phase 5: User Story 3 - Track Object States and Movements (Priority: P2)

**Goal**: Enable users to record observations and object states as part of ongoing organization.

**Independent Test**: Persist an observation and a state record, then verify the history is available.

- [x] T026 [P] Implement `Observation` domain entity in `backend/src/main/java/com/treco-dex-api/domain/model/Observation.java`
- [x] T027 [P] Implement `ObjectStateRecord` domain entity in `backend/src/main/java/com/treco-dex-api/domain/model/ObjectStateRecord.java`
- [x] T028 Implement observation and state repositories in `backend/src/main/java/com/treco-dex-api/infrastructure/persistence/`
- [x] T029 Implement `ObservationController` and `ObjectStateController` in `backend/src/main/java/com/treco-dex-api/api/controller/`
- [x] T030 Implement validation DTOs for observations and state records in `backend/src/main/java/com/treco-dex-api/api/dto/`
- [x] T031 Add observation and state workflow tests in `backend/src/test/java/com/treco-dex-api/`

---

## Phase 6: User Story 4 - Initial Organizational Assistance (Priority: P3)

**Goal**: Provide initial contextual descriptions and assistance without adding complex AI workflows.

**Independent Test**: Retrieve an object and verify the response includes a contextual description field.

- [x] T032 Implement AI enrichment service interface in `backend/src/main/java/com/treco-dex-api/application/service/AiEnrichmentService.java`
- [x] T033 Implement simple description generation in `backend/src/main/java/com/treco-dex-api/application/service/DescriptionService.java`
- [x] T033a [P] Implement hybrid `RecommendationService.java` (Factual DB search + fully decoupled LangChain4j Chat model with OpenRouter/OpenAI support) and the Redis-based conversational state machine flow.
- [x] T033b [P] Implement `VisionService.java` for multimodal visual search (decoupled LangChain4j Vision model supporting OpenRouter/OpenAI) and the `/api/objects/visual-search` endpoint.
- [x] T034 Add contextual description field to object response DTO in `backend/src/main/java/com/treco-dex-api/api/dto/ObjectSpeciesResponse.java`
- [x] T035 Add unit tests for description generation in `backend/src/test/java/com/treco-dex-api/`

---

## Phase 7: Media & Async Processing

**Purpose**: Support image upload and build the async foundation for future processing.

- [x] T036 Implement `MediaAsset` domain entity in `backend/src/main/java/com/treco-dex-api/domain/model/MediaAsset.java`
- [x] T037 Implement `MediaStorageService` in `backend/src/main/java/com/treco-dex-api/application/service/MediaStorageService.java`
- [x] T038 Implement `MediaController` upload and retrieval endpoints in `backend/src/main/java/com/treco-dex-api/api/controller/MediaController.java`
- [x] T039 Implement media repository support in `backend/src/main/java/com/treco-dex-api/infrastructure/persistence/`
- [x] T040 Implement basic Kafka event producer for media upload events in `backend/src/main/java/com/treco-dex-api/infrastructure/messaging/`
- [x] T041 Implement worker scaffold for async event consumption in `backend/src/main/java/com/treco-dex-api/infrastructure/messaging/`
- [x] T042 Add media upload and async event tests in `backend/src/test/java/com/treco-dex-api/`

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Harden the MVP with documentation, testing, validation, and security.

- [x] T043 [P] Document REST APIs and contracts in `specs/001-treco-dex-api-product-spec/contracts/api-contract.md`
- [x] T043a [P] Configure Gradle Checkstyle and KtLint plugins for static analysis validation in `backend/build.gradle.kts`
- [x] T044 [P] Add runtime validation for request and response payloads in `backend/src/main/java/com/treco-dex-api/api/validation/`
- [x] T045 [P] Add structured JSON logs and metrics instrumentation in `backend/src/main/java/com/treco-dex-api/infrastructure/telemetry/`
- [x] T046 [P] Implement rate limiting and audit event logging for critical endpoints in `backend/src/main/java/com/treco-dex-api/api/security/`
- [x] T047 [P] Add end-to-end integration tests for full MVP workflows in `backend/src/test/java/com/treco-dex-api/integration/`
- [x] T049 Add search performance and relevance validation tests for `SC-002` and `SC-004` in `backend/src/test/java/com/treco-dex-api/integration/`
- [x] T050 Add object state accuracy validation tests for `SC-003` in `backend/src/test/java/com/treco-dex-api/integration/`

---

## Dependencies & Execution Order

- **Phase 1: Setup** can begin immediately.
- **Phase 2: Foundational** depends on Phase 1 completion and blocks all user stories.
- **Phase 3+** can begin after Phase 2 is complete.
- **User Story 1 (P1)** can begin immediately after foundation and is the MVP-critical path.
- **User Story 2 (P1)** can proceed in parallel with User Story 1 once the foundation is ready.
- **User Story 3 (P2)** can begin after foundation and may integrate with User Story 1/2 but must remain independently testable.
- **User Story 4 (P3)** is lower priority and can be implemented after core search and state tracking are available.
- **Phase 7: Media & Async Processing** can begin after core storage and persistence are in place.
- **Phase 8: Polish & Cross-Cutting Concerns** should be completed after the main API and domain workflows are implemented.

## Parallel Execution Examples

- **Phase 1**: `T003`, `T004`, and `T005` can run in parallel.
- **Phase 2**: `T007`, `T010`, and `T012` can run in parallel.
- **User Story 1**: `T013` and `T014` can be built in parallel; `T019` and `T020` can run while service/controller tasks are implemented.
- **User Story 2**: `T021` and `T022` can proceed in parallel with `T023`.
- **User Story 3**: `T026` and `T027` can be implemented in parallel.
- **Phase 8**: `T043`, `T044`, and `T045` can be executed in parallel.

## Implementation Strategy

1. Complete **Phase 1** and **Phase 2** to establish a stable backend foundation.
2. Deliver **User Story 1** first as the MVP core, then validate the object registration/catalog workflow.
3. Add **User Story 2** and then **User Story 3** to expand retrieval and tracking capabilities.
4. Add **User Story 4** for initial organizational assistance as a lower-priority enhancement.
5. Implement **Phase 7** media and async support once core domain flows are operational.
6. Finish with **Phase 8** polish, security hardening, documentation, and validation.

## MVP Scope Confirmation

This task set is limited to the MVP scope defined by the feature specification and the plan: authentication, object cataloging, habitat management, observation tracking, basic search, media upload, lightweight async readiness, initial contextual descriptions, and minimal observability. Advanced automation, complex AI workflows, multi-user collaboration, smart home integration, and full RAG pipelines are excluded.
