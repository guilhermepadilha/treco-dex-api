# Tasks: TrecoDex Backend Platform MVP

**Input**: Design documents from `specs/001-trecodex-product-spec/`  
**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/api-contract.md`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Initialize backend structure, build configuration, and local runtimes.

- [x] T001 Create backend module layout in `backend/` with `build.gradle.kts`, `settings.gradle.kts`, `src/`, and `docker/`
- [x] T002 Configure Gradle Kotlin DSL build files in `backend/build.gradle.kts` and `backend/settings.gradle.kts` with Spring Boot dependencies
- [x] T003 [P] Create package structure under `backend/src/main/java/com/trecodex/` for `api/`, `application/`, `domain/`, and `infrastructure/`
- [x] T004 [P] Add `backend/src/main/resources/application.yaml` and initial migration folder `backend/src/main/resources/db/migrations/`
- [x] T005 [P] Create local container bootstrap files in `backend/docker/Dockerfile` and `backend/docker/docker-compose.yml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Implement core infrastructure required before user story work begins.

- [ ] T006 Setup PostgreSQL migration framework and initial schema script in `backend/src/main/resources/db/migrations/`
- [ ] T007 [P] Implement JWT authentication support in `backend/src/main/java/com/trecodex/api/security/`
- [ ] T008 [P] Configure environment-based application properties in `backend/src/main/resources/application.yaml`
- [ ] T009 Implement base REST API routing and controller advice in `backend/src/main/java/com/trecodex/api/config/` and `backend/src/main/java/com/trecodex/api/error/`
- [ ] T010 [P] Implement structured JSON logging and correlation IDs in `backend/src/main/java/com/trecodex/infrastructure/telemetry/`
- [ ] T011 Create base domain repository interfaces and core entity classes for `ObjectSpecies`, `Habitat`, `Environment`, `Observation`, `ObjectStateRecord`, `UserAccount`, and `MediaAsset` in `backend/src/main/java/com/trecodex/domain/`
- [ ] T012 Create test skeletons in `backend/src/test/java/com/trecodex/` for unit and integration work
- [ ] T048 Implement `Environment` domain entity, persistence support, and habitat-to-environment association in `backend/src/main/java/com/trecodex/domain/model/` and `backend/src/main/java/com/trecodex/infrastructure/persistence/`

---

## Phase 3: User Story 1 - Register and Catalog Objects (Priority: P1)

**Goal**: Enable users to register object species, persist object metadata, and manage habitats.

**Independent Test**: Create an object species with habitat and verify it can be retrieved via REST CRUD.

- [ ] T013 [P] Implement `ObjectSpecies` domain entity in `backend/src/main/java/com/trecodex/domain/model/ObjectSpecies.java`
- [ ] T014 [P] Implement `Habitat` domain entity with optional `Environment` association in `backend/src/main/java/com/trecodex/domain/model/Habitat.java`
- [ ] T015 Implement `ObjectSpeciesRepository` and `HabitatRepository` in `backend/src/main/java/com/trecodex/infrastructure/persistence/`
- [ ] T016 Implement `ObjectSpeciesService` in `backend/src/main/java/com/trecodex/application/service/ObjectSpeciesService.java`
- [ ] T017 Implement `ObjectSpeciesController` CRUD endpoints in `backend/src/main/java/com/trecodex/api/controller/ObjectSpeciesController.java`
- [ ] T018 Implement request/response DTOs and validation in `backend/src/main/java/com/trecodex/api/dto/`
- [ ] T019 Add unit tests for object service and controller in `backend/src/test/java/com/trecodex/application/` and `backend/src/test/java/com/trecodex/api/`
- [ ] T020 Add integration tests for object registration and retrieval in `backend/src/test/java/com/trecodex/integration/`

---

## Phase 4: User Story 2 - Locate Objects Quickly (Priority: P1)

**Goal**: Implement contextual search so users can find objects by name, habitat, and state.

**Independent Test**: Search objects and verify responses include habitat and last observation context.

- [ ] T021 [P] Implement `SearchService` in `backend/src/main/java/com/trecodex/application/service/SearchService.java`
- [ ] T022 Implement `SearchController` endpoint in `backend/src/main/java/com/trecodex/api/controller/SearchController.java`
- [ ] T023 Implement filtered search repository queries in `backend/src/main/java/com/trecodex/infrastructure/persistence/SearchRepository.java`
- [ ] T024 Implement contextual object summary DTOs in `backend/src/main/java/com/trecodex/api/dto/`
- [ ] T025 Add search integration tests in `backend/src/test/java/com/trecodex/integration/`

---

## Phase 5: User Story 3 - Track Object States and Movements (Priority: P2)

**Goal**: Enable users to record observations and object states as part of ongoing organization.

**Independent Test**: Persist an observation and a state record, then verify the history is available.

- [ ] T026 [P] Implement `Observation` domain entity in `backend/src/main/java/com/trecodex/domain/model/Observation.java`
- [ ] T027 [P] Implement `ObjectStateRecord` domain entity in `backend/src/main/java/com/trecodex/domain/model/ObjectStateRecord.java`
- [ ] T028 Implement observation and state repositories in `backend/src/main/java/com/trecodex/infrastructure/persistence/`
- [ ] T029 Implement `ObservationController` and `ObjectStateController` in `backend/src/main/java/com/trecodex/api/controller/`
- [ ] T030 Implement validation DTOs for observations and state records in `backend/src/main/java/com/trecodex/api/dto/`
- [ ] T031 Add observation and state workflow tests in `backend/src/test/java/com/trecodex/`

---

## Phase 6: User Story 4 - Initial Organizational Assistance (Priority: P3)

**Goal**: Provide initial contextual descriptions and assistance without adding complex AI workflows.

**Independent Test**: Retrieve an object and verify the response includes a contextual description field.

- [ ] T032 Implement AI enrichment service interface in `backend/src/main/java/com/trecodex/application/service/AiEnrichmentService.java`
- [ ] T033 Implement simple description generation in `backend/src/main/java/com/trecodex/application/service/DescriptionService.java`
- [ ] T034 Add contextual description field to object response DTO in `backend/src/main/java/com/trecodex/api/dto/ObjectSpeciesResponse.java`
- [ ] T035 Add unit tests for description generation in `backend/src/test/java/com/trecodex/`

---

## Phase 7: Media & Async Processing

**Purpose**: Support image upload and build the async foundation for future processing.

- [ ] T036 Implement `MediaAsset` domain entity in `backend/src/main/java/com/trecodex/domain/model/MediaAsset.java`
- [ ] T037 Implement `MediaStorageService` in `backend/src/main/java/com/trecodex/application/service/MediaStorageService.java`
- [ ] T038 Implement `MediaController` upload and retrieval endpoints in `backend/src/main/java/com/trecodex/api/controller/MediaController.java`
- [ ] T039 Implement media repository support in `backend/src/main/java/com/trecodex/infrastructure/persistence/`
- [ ] T040 Implement basic Kafka event producer for media upload events in `backend/src/main/java/com/trecodex/infrastructure/messaging/`
- [ ] T041 Implement worker scaffold for async event consumption in `backend/src/main/java/com/trecodex/infrastructure/messaging/`
- [ ] T042 Add media upload and async event tests in `backend/src/test/java/com/trecodex/`

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Harden the MVP with documentation, testing, validation, and security.

- [ ] T043 [P] Document REST APIs and contracts in `specs/001-trecodex-product-spec/contracts/api-contract.md`
- [ ] T044 [P] Add runtime validation for request and response payloads in `backend/src/main/java/com/trecodex/api/validation/`
- [ ] T045 [P] Add structured JSON logs and metrics instrumentation in `backend/src/main/java/com/trecodex/infrastructure/telemetry/`
- [ ] T046 [P] Implement rate limiting and audit event logging for critical endpoints in `backend/src/main/java/com/trecodex/api/security/`
- [ ] T047 [P] Add end-to-end integration tests for full MVP workflows in `backend/src/test/java/com/trecodex/integration/`
- [ ] T049 Add search performance and relevance validation tests for `SC-002` and `SC-004` in `backend/src/test/java/com/trecodex/integration/`
- [ ] T050 Add object state accuracy validation tests for `SC-003` in `backend/src/test/java/com/trecodex/integration/`

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
