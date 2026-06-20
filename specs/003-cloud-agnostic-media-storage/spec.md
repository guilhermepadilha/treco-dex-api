# Feature Spec: Cloud-Agnostic Media Storage

## 1. Goal
Refactor the media upload infrastructure to transition from saving raw Base64 image strings directly in the PostgreSQL database to a cloud-agnostic object storage model. This ensures the database is not bloated and the system adheres to the original SDD, following SOLID principles to allow swapping storage providers (AWS S3, Cloudinary, Local Storage) via configuration.

## 2. Requirements

### 2.1 Core Capabilities
- Parse the Base64 image payload from the mobile client into a binary file/stream.
- Upload the binary file to an external Object Storage service.
- Retrieve a public (or pre-signed) URL for the uploaded image.
- Save ONLY the short URL in the PostgreSQL `media_asset` table.

### 2.2 SOLID Architecture (Agnostic Design)
- **Dependency Inversion Principle (DIP):** The `MediaStorageService` must not depend on concrete implementations like `AwsS3StorageProvider` or `CloudinaryStorageProvider`. It must depend on an abstraction `CloudStorageProvider`.
- **Open/Closed Principle (OCP):** Adding a new cloud provider in the future (e.g., Azure Blob Storage) should not require modifying the core `MediaStorageService`. We just create a new class implementing `CloudStorageProvider`.
- **Liskov Substitution Principle (LSP):** All storage providers must guarantee the same contract: receiving a file and returning a valid URL string.

### 2.3 Configuration-Driven
- The active storage provider must be injected at runtime via Spring Boot's `@ConditionalOnProperty` or a factory configuration based on `application.yaml` properties (e.g., `app.storage.provider=s3` or `app.storage.provider=local`).

## 3. Data Model Impact
- `media_asset` table: The `url` column will now store standard HTTP URLs (e.g., `https://s3.amazonaws.com/trecodex/images/uuid.png`) instead of `data:image/png;base64,...`.
- `V2__alter_media_asset_url_to_text.sql`: Keep it as TEXT or revert it later, but the stored content will be short.

## 4. Dependencies
- An AWS S3 client dependency (e.g., `software.amazon.awssdk:s3`).
- Or any compatible library depending on the first implemented provider.
