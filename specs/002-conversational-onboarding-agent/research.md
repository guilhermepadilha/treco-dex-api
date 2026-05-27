# Research & Technology Decisions: Conversational Onboarding Agent

## Tech Decisions

### Decision 1: Structured AI Parsing via LangChain4j Prompts
*   **Selected Approach**: LangChain4j system prompting returning structured JSON outputs using `AiServices` and custom POJOs.
*   **Rationale**: Ensures type-safe extraction of parameters (`confirmed`, `objectName`, `habitatName`) without manual JSON parsing from text responses.
*   **Alternatives Considered**: 
    *   *Manual Regex & Keyword Matching*: Rejected as fragile and incapable of parsing complex informal phrases like *"não, na verdade é X"*.
    *   *Direct REST call to OpenAI*: Rejected due to high vendor lock-in and manual parsing overhead.

### Decision 2: State Tracking via Redis
*   **Selected Approach**: Storing the ephemeral conversation context as JSON in Redis with a 30-minute Time-To-Live (TTL).
*   **Rationale**: Onboarding is a temporary multi-step process. Using PostgreSQL for this adds unnecessary write overhead and database clutter, whereas Redis is perfect for fast, self-cleaning session cache.
*   **Alternatives Considered**: 
    *   *PostgreSQL session table*: Rejected because of database bloat and lack of automatic self-cleanup mechanisms.

### Decision 3: AI Model Resilience Configuration
*   **Selected Approach**: Decoupled primary model and secondary model with auto-failover policy.
*   **Rationale**: Rate-limits and temporary outages on free AI API providers require a robust retry/fallback strategy to avoid breaking the scanner onboarding flow.
