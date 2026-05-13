# Data Model: TrecoDex Backend Platform

## Core Entities

### ObjectSpecies
Represents a cataloged class of objects rather than a specific physical instance.

- id: UUID
- name: String
- description: String
- category: String
- sizeDescriptor: String? (e.g., AA, AAAA, pequeno, grande)
- primaryHabitatId: UUID?  
- environmentId: UUID?  
- defaultState: ObjectState
- visualReferenceId: UUID?
- metadata: JSONB
- createdAt: Instant
- updatedAt: Instant

### Habitat
Represents the intended storage location for an object.

- id: UUID
- name: String
- description: String?
- environmentId: UUID?
- type: String? (e.g., gaveta, armário, caixa)
- visualReferenceId: UUID?
- createdAt: Instant
- updatedAt: Instant

### Environment
Represents high-level organizational areas.

- id: UUID
- name: String
- description: String?
- createdAt: Instant
- updatedAt: Instant

### Observation
Represents user-provided contextual records about an object species.

- id: UUID
- objectSpeciesId: UUID
- note: String
- observationType: String? (e.g., "último avistamento", "fora do lugar")
- observedAt: Instant
- createdBy: UUID
- createdAt: Instant
- updatedAt: Instant

### ObjectStateRecord
Represents explicit state declarations made by the user.

- id: UUID
- objectSpeciesId: UUID
- state: ObjectState
- reason: String?
- effectiveAt: Instant
- createdBy: UUID
- createdAt: Instant

### MediaAsset
Represents visual media associated with objects or habitats.

- id: UUID
- ownerId: UUID
- objectSpeciesId: UUID?
- habitatId: UUID?
- url: String  
  *A reference or location for the stored media, not the raw image bytes.*
- type: String
- metadata: JSONB
- createdAt: Instant
- updatedAt: Instant

> Note: The actual photo file should be stored outside the transactional database in object storage or another dedicated media store. The database keeps the metadata and access path.

### UserAccount
Represents authenticated users of the backend.

- id: UUID
- email: String
- hashedPassword: String
- displayName: String?
- createdAt: Instant
- updatedAt: Instant

### AuditEvent
Tracks important domain changes for traceability.

- id: UUID
- entityType: String
- entityId: UUID
- action: String
- payload: JSONB
- performedBy: UUID
- occurredAt: Instant

## Domain Value Objects

### ObjectState
Enum values:
- IN_HABITAT
- OUT_OF_HABITAT
- MISSING
- RECENTLY_SEEN
- UNKNOWN

### HabitatRefuge
Represents temporary out-of-place context.

- habitatName: String
- description: String?
- observedAt: Instant

## Relationships

- ObjectSpecies → Habitat (many-to-one via primaryHabitatId)
- ObjectSpecies → Environment (many-to-one via environmentId)
- Habitat → Environment (many-to-one)
- Observation → ObjectSpecies (many-to-one)
- ObjectStateRecord → ObjectSpecies (many-to-one)
- MediaAsset → ObjectSpecies / Habitat (optional many-to-one)

## Validation Rules

- ObjectSpecies.name is required and must be unique within owner scope.
- Habitat.name is required and should be human-readable.
- Observation.note is required for meaningful context.
- ObjectStateRecord.state must be one of the defined enum values.
- MediaAsset.url must point to an approved storage location.

## Notes on Model Design

- The MVP treats catalog entries as species-like records; physical duplicates are represented by the same ObjectSpecies entry.
- Temporary or out-of-place metadata is captured as user-provided observations rather than multiple habitat assignments.
- The model preserves user-driven state declarations rather than deriving states automatically.
- AuditEvent and Observation records provide the traceability demanded by the constitution.
