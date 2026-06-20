# Tasks: Cloud-Agnostic Media Storage

**Input**: Design documents from `/specs/003-cloud-agnostic-media-storage/`
**Prerequisites**: plan.md (required), spec.md (required)

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Build configuration, dependencies, and local MinIO container

- [x] T001 Add MinIO service and bucket initialization to docker-compose.yml
- [x] T002 Configure AWS SDK S3 dependency in build.gradle.kts

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core interfaces and conditional configuration classes for seamless multi-cloud support

- [x] T003 [P] Create CloudStorageProvider interface in src/main/java/com/treco/dex/api/application/service/CloudStorageProvider.java
- [x] T004 [P] Implement S3StorageProvider with endpoint override support (for MinIO) in src/main/java/com/treco/dex/api/infrastructure/storage/S3StorageProvider.java
- [x] T005 [P] Create StorageConfig configurations for bean conditional loading in src/main/java/com/treco/dex/api/infrastructure/storage/StorageConfig.java
- [x] T006 Update application properties (MinIO for dev, AWS for prod) in src/main/resources/application.yaml

---

## Phase 3: User Story 1 - Cloud-Agnostic Media Upload (Priority: P1)

**Goal**: Integrate the media storage service to decode base64 strings and save them using the active storage provider.

**Independent Test**: Upload media via `/api/media` and verify the stored URL is a short local/S3 URL instead of a raw base64 string, and the database record is correctly created.

### Implementation for User Story 1

- [x] T007 [US1] Refactor MediaStorageService to inject CloudStorageProvider and upload base64 images in src/main/java/com/treco/dex/api/application/service/MediaStorageService.java
- [x] T008 [P] [US1] Create MediaStorageServiceTest to mock CloudStorageProvider and verify behavior in src/test/java/com/treco/dex/api/application/service/MediaStorageServiceTest.java

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Final validations and documentation updates

- [x] T009 Document cloud-agnostic storage setup and MinIO local environment in specs/003-cloud-agnostic-media-storage/quickstart.md
