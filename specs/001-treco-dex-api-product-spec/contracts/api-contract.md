# API Contract: Treco-Dex-API Backend Platform

## Base API

- Base path: `/api/v1`
- Authentication: `Authorization: Bearer <token>`
- Content type: `application/json`

## Authentication

### POST /api/v1/auth/login

Request:
```json
{
  "email": "user@example.com",
  "password": "password"
}
```

Response:
```json
{
  "accessToken": "<jwt>",
  "expiresIn": 3600
}
```

## Object Species

### GET /api/v1/objects

Response:
```json
[
  {
    "id": "uuid",
    "name": "PILHA",
    "description": "Conjunto de pilhas AA/AAAA",
    "category": "Eletrônicos",
    "sizeDescriptor": "AA",
    "primaryHabitatId": "uuid",
    "defaultState": "IN_HABITAT"
  }
]
```

### POST /api/v1/objects

Request:
```json
{
  "name": "PILHA",
  "description": "Conjunto de pilhas",
  "category": "Acessórios",
  "sizeDescriptor": "AA",
  "primaryHabitatId": "uuid"
}
```

Response:
```json
{
  "id": "uuid"
}
```

## Habitats

### GET /api/v1/habitats

Response:
```json
[
  {
    "id": "uuid",
    "name": "gaveta da escrivaninha",
    "description": "Local de armazenamento principal"
  }
]
```

### POST /api/v1/habitats

Request:
```json
{
  "name": "gaveta da escrivaninha",
  "description": "Gaveta onde ficam os acessórios de mesa"
}
```

## Observations

### POST /api/v1/observations

Request:
```json
{
  "objectSpeciesId": "uuid",
  "note": "Visto na mesa do escritório ontem",
  "observationType": "último avistamento",
  "observedAt": "2026-05-12T10:00:00Z"
}
```

Response:
```json
{
  "id": "uuid"
}
```

## State Records

### POST /api/v1/object-state-records

Request:
```json
{
  "objectSpeciesId": "uuid",
  "state": "MISSING",
  "reason": "Não foi encontrado no local habitual",
  "effectiveAt": "2026-05-12T10:00:00Z"
}
```

## Search

### GET /api/v1/search

Query parameters:
- `q` (text)
- `habitatId`
- `state`
- `category`

Response: object summary list with habitat and last observation context.

## Contract Notes

- All APIs must use explicit request and response schemas.
- OpenAPI documentation must be generated from the REST controllers.
- Versioning must be supported through the `/api/v1` prefix.
- Validation errors should return structured payloads with clear field-level messages.
