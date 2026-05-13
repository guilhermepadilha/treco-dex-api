# Research: TrecoDex Backend Platform

## Decisions

- Decision: Implement the backend as a modular monolith with event-driven readiness.
  - Rationale: Minimizes operational complexity for MVP while preserving clear boundaries for future decomposition.
  - Alternatives considered: microservices from day one (rejected for premature complexity), single-layer monolith (rejected for poor future scalability and modularity).

- Decision: Use Java 21 LTS + Spring Boot 3.x for backend development.
  - Rationale: Stable long-term support, mature JVM ecosystem, broad corporate adoption, strong support for modularization, concurrency and observability.
  - Alternatives considered: Node.js / Python (rejected for lower enterprise durability and weaker JVM-native observability), Kotlin (viable, but Java chosen for broader team familiarity in this context).

- Decision: Use PostgreSQL with pgvector extension for initial database strategy.
  - Rationale: Provides strong transactional support, structured domain persistence, and a low-friction path to vector search without a separate vector DB.
  - Alternatives considered: dedicated vector DBs (rejected for operational overhead in MVP), document stores (rejected for weaker relational modeling of habitats and observations).

- Decision: Add Redis for cache, session support, rate limiting and temporary workflow state.
  - Rationale: Provides a lightweight distributed cache layer and improves responsiveness for query-heavy operations.

- Decision: Use Kafka for messaging and async event readiness.
  - Rationale: Supports future domain events, AI workflows, and workflow orchestration without forcing a distributed architecture at MVP.

- Decision: Treat AI as an assistive, decoupled layer.
  - Rationale: Keeps the domain rules pure and avoids core business dependency on specific AI providers.
  - Alternatives considered: embedding AI directly into domain logic (rejected for brittleness and opacity).

- Decision: Keep observations manual for MVP.
  - Rationale: User-provided contextual clues are sufficient for the initial product experience, and automation is a higher-risk future capability.
  - Alternatives considered: sensor-based or automatic observation capture (deferred due to research and operational complexity).

- Decision: Model object identity as species/type-level catalog entries.
  - Rationale: Aligns with the TrecoDex Pokédex metaphor and simplifies the MVP by avoiding per-unit tracking of indistinguishable duplicates.
  - User-facing differentiation can still occur through size/characteristics, while the catalog remains type-centric.

- Decision: Use a single primary habitat per object and treat temporary locations as optional "refuge" metadata.
  - Rationale: Simplifies habitat modeling while preserving the ability to capture out-of-place context.

- Decision: Keep humor/personality as an optional Pokédex-like voice.
  - Rationale: Supports the desired playful experience without forcing humorous output on users who prefer a formal tone.

## Alternatives Considered

- Alternative: Build with Kotlin and Micronaut.
  - Why rejected: additional language semantics complexity and weaker JVM ecosystem support for some corporate integration patterns.

- Alternative: Implement vector search with a managed service.
  - Why rejected: added vendor dependency and operational cost for MVP.

- Alternative: Support multi-user sharing in MVP.
  - Why rejected: changes core authentication, authorization, and data isolation requirements, delaying delivery of the initial product.

## Clarifications Resolved

- Object identity is species-based, not instance-based.
- Habitats are single primary storage locations; temporary refuges are optional contextual metadata.
- Observations are manual in MVP; history is informational and not subject to complex lifecycle rules.
- States are user-declared; the backend should accept explicit state labels and optionally query the user once per day for lost items.
- AI assistance is limited to gentle prompts and suggestions; no automatic catalog updates without user confirmation.
- Humor is a personality layer; it can be disabled for formal output.
- MVP excludes complex automation, smart-home integration, and multi-user sharing.
- Authentication is required for the backend; multi-user sharing is a future evolution.
- Search relevance metrics and advanced semantic ranking are deferred beyond MVP.

## Research Summary

The current plan is based on a stable backend platform approach: a Java/Spring Boot modular monolith, PostgreSQL + pgvector, Redis, Kafka, and a decoupled AI layer. The plan minimizes early complexity while preserving clear future paths for async workflows, search enrichment, and mobile integration.