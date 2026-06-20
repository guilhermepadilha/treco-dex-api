# Implementation Plan: Cloud-Agnostic Media Storage

**Branch**: `001-add-trecodex` | **Date**: 2026-06-20 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/003-cloud-agnostic-media-storage/spec.md`

## Summary

O objetivo deste plano é estabelecer a arquitetura técnica e o design de software para a implementação do **Cloud-Agnostic Media Storage** no backend `treco-dex-api`. Esta refatoração visa resolver o salvamento indevido de strings Base64 gigantes na coluna `url` da tabela `media_asset` do PostgreSQL, migrando para um armazenamento baseado em objetos.

Para garantir paridade entre ambientes e o mínimo de esforço ao migrar entre provedores de nuvem (AWS, GCP, Azure), utilizaremos uma arquitetura fortemente baseada na interface `CloudStorageProvider`. O ambiente local utilizará o **MinIO** (S3-compatible API), permitindo que a mesma implementação de S3 usada na AWS em produção seja executada localmente, bastando sobrescrever o endpoint via configuração.

## Technical Context

**Language/Version**: Java 21 (Spring Boot 3.3.x)  
**Primary Dependencies**: AWS SDK S3 (`software.amazon.awssdk:s3:2.25.11`), Lombok  
**Storage**: AWS S3 (Prod) / MinIO (Local Dev), PostgreSQL (para metadados do `MediaAsset` contendo URLs curtas)  
**Testing**: JUnit 5, Mockito, Spring Boot Test  
**Target Platform**: Linux server  
**Project Type**: REST Web Service  
**Performance Goals**: Decodificação Base64 e upload < 500ms  
**Constraints**: A configuração de nuvem deve ser trocável modificando apenas a propriedade `app.storage.provider`.

## Constitution Check

*GATE: Passed. O plano cumpre todas as diretrizes da Constitution do Projeto.*

- **Domain-Driven Design (DDD) / Hexagonal Architecture**: A interface `CloudStorageProvider` atua como uma porta de saída (port) para armazenamento físico de mídias, permitindo suporte futuro nativo a GCP ou Azure adicionando novos Adapters, sem tocar no core.
- **Clean Architecture / Infrastructure Independence**: A comunicação com a nuvem é encapsulada em `infrastructure/storage/S3StorageProvider`.
- **Cloud-Native Readiness**: Utilização do MinIO local via Docker container aproxima o ambiente de desenvolvimento à realidade de produção (S3 API).

## Project Structure

### Documentation (this feature)

```text
specs/003-cloud-agnostic-media-storage/
├── spec.md              # Feature specification
├── plan.md              # This plan
└── tasks.md             # Implementation tasks
```

### Source Code (repository root)

```text
src/main/java/com/treco/dex/api/
├── application/
│   └── service/
│       ├── MediaStorageService.java             # Refatorado para usar CloudStorageProvider
│       └── CloudStorageProvider.java            # Interface de abstração (Port)
└── infrastructure/
    └── storage/
        ├── S3StorageProvider.java               # Adapter S3 (Suporta AWS e MinIO)
        └── StorageConfig.java                   # Inicialização condicional dos beans
```

**Structure Decision**: Apenas o `S3StorageProvider` é necessário, graças ao uso do MinIO localmente. Novos provedores como `GcpStorageProvider` ou `AzureStorageProvider` podem ser injetados em `infrastructure/storage` no futuro, mantendo a arquitetura completamente limpa e agnóstica.
