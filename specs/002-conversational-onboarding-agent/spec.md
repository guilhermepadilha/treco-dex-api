# Feature Specification: Conversational Onboarding Agent

**Feature Branch**: `002-conversational-onboarding-agent`  
**Created**: 2026-05-27  
**Status**: Draft  
**Input**: User description: "Implementar um Agente de IA Conversacional no Backend do TrecoDex que processe as mensagens enviadas pelo usuário no Onboarding do Scanner. O agente deve analisar a linguagem natural do usuário (ex: 'Sim é um mouse e ele vive na mesa do escritório') para confirmar se o objeto sugerido está correto (ex: se é de fato um 'mouse') e extrair o habitat (local) onde o usuário deseja guardar o treco (ex: 'mesa do escritório'). O agente deve salvar esses dados no cache do Redis da sessão activa de onboarding e responder de maneira amigável."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Conversational Intent Parsing & Info Extraction (Priority: P1)

As a user onboarding a new unrecognized object, I want the system to parse my natural language messages so that I do not have to fill out complex forms and can confirm classifications or describe locations conversationally.

**Why this priority**: Core AI capability that resolves the conversational disorganization and extracts precise parameters from informal natural language.

**Independent Test**: Can be fully tested using unit/integration tests with predefined text inputs (e.g., "Sim, é um mouse e fica na mesa", "Não, é um teclado e fica na gaveta") and verifying the extracted structured JSON contains the correct confirmation boolean, correct object name, and correct habitat.

**Acceptance Scenarios**:

1. **Given** an active Redis onboarding session for a visual classification "mouse", **When** the user says "Sim, é um mouse e ele vive na mesa do escritório", **Then** the AI parser returns a structured outcome indicating: `confirmed = true`, `resolvedObjectName = "mouse"`, and `extractedHabitatName = "mesa do escritório"`.
2. **Given** an active Redis onboarding session for a visual classification "caneca", **When** the user says "Não, é um copo de vidro e eu guardo no armário da cozinha", **Then** the AI parser returns a structured outcome indicating: `confirmed = false`, `resolvedObjectName = "copo de vidro"`, and `extractedHabitatName = "armário da cozinha"`.
3. **Given** an active Redis onboarding session, **When** the user provides ambiguous input without confirming the object (e.g. "o que você acha?"), **Then** the AI parser requests clarification regarding the object identity.

---

### User Story 2 - Conversational Onboarding Endpoints (Priority: P1)

As a client application developer, I want a dedicated chat onboarding API endpoint so that I can send the user's messages to the backend and advance the onboarding state machine.

**Why this priority**: Required interface to connect the mobile application scanner interface to the backend conversational agent.

**Independent Test**: Can be tested by executing a POST request to `/api/objects/chat-onboarding` with a session ID and a chat message, verifying the HTTP status is 200, the Redis state is updated, and the structured response contains the AI's friendly reply message.

**Acceptance Scenarios**:

1. **Given** a user has a valid visual search session, **When** they POST to `/api/objects/chat-onboarding` with a message, **Then** the system returns the AI response text and the updated session status.
2. **Given** the onboarding details are complete (both object name and habitat are resolved), **When** a chat message completes the flow, **Then** the system automatically registers the new object and habitat in the database and terminates the Redis session.

---

## Edge Cases

- **Ambiguous Inputs**: The user inputs a message that doesn't clearly confirm or reject the object name, or doesn't mention a habitat.
  * *Resolution*: The AI agent should friendly ask the user to clarify: "Desculpe, não entendi se é de fato um [Objeto]. Você poderia confirmar, e me dizer onde deseja guardá-lo?"
- **Missing Redis Session**: The client calls the onboarding endpoint but the session has expired or is invalid.
  * *Resolution*: The system returns a user-friendly error response prompting them to scan the object again.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST expose a POST endpoint at `/api/objects/chat-onboarding` that consumes a message and session ID.
- **FR-002**: System MUST retrieve and update the active conversational onboarding session state in Redis.
- **FR-003**: System MUST utilize LangChain4j and a structured LLM prompt to parse user natural language messages.
- **FR-004**: System MUST extract: `confirmed` (boolean), `objectName` (string, corrected if not confirmed), and `habitatName` (string).
- **FR-005**: System MUST complete the registration of the new object and habitat in the database once both details are successfully resolved.
- **FR-006**: System MUST return a friendly, conversational reply text alongside the structured state updates.

### Key Entities *(include if feature involves data)*

- **ConversationSession**: Extends the existing session state to track the active onboarding context, chat history, resolved object name, and resolved habitat name.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: The AI agent correctly extracts the object name and habitat from user natural language messages in 90% of validation scenarios.
- **SC-002**: The chat onboarding API endpoint responds under 3 seconds per request.
- **SC-003**: Successfully registered objects are instantly visible in the main catalog after onboarding completes.

## Assumptions

- The backend will reuse the existing AI model configurations (primary/fallback) for the LangChain4j chat service.
- The Redis session will expire after 30 minutes of inactivity.
