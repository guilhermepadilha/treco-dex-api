# Tasks: LLM Robustness — Conversational Onboarding Agent

**Input**: Análise diagnóstica da falha de parsing do `ChatOnboardingService`  
**Contexto**: O agente conversacional está retornando "Desculpe, não consegui processar sua mensagem." para toda mensagem em linguagem natural, pois o LLM não é forçado a responder em JSON, não recebe o histórico da conversa e sobrecarrega uma única chamada com múltiplas responsabilidades.  
**Spec de referência**: `specs/002-conversational-onboarding-agent/spec.md`  
**Prerequisites**: `tasks.md` (T001–T008 concluídas)

---

## Phase 1: Garantir JSON Mode (Correção Crítica)

> Resolve a causa-raiz do fallback loop. Custo baixo, impacto imediato.

- [x] **T009** Adicionar `responseFormat("json_object")` ao bean `OpenAiChatModel` em `AiConfig.java`.
  - **Arquivo**: `src/main/java/com/treco/dex/api/api/config/AiConfig.java`
  - **O que fazer**: Adicionar `.responseFormat("json_object")` ao builder do `OpenAiChatModel`.
  - **Por quê**: Sem essa configuração, o LLM responde em texto livre e o LangChain4j falha ao desserializar para `OnboardingIntent`, caindo sempre no `catch` com o fallback.
  - **Critério de aceite**: O log `AI agent parsing failed` não deve mais aparecer para mensagens válidas em linguagem natural.

- [x] **T010** Reforçar o `@SystemMessage` do `ConversationAgent` com instrução explícita de JSON-only.
  - **Arquivo**: `src/main/java/com/treco/dex/api/application/service/ConversationAgent.java`
  - **O que fazer**: Adicionar ao final do `@SystemMessage`: `"IMPORTANTE: Responda APENAS com um JSON válido contendo os campos: confirmed, objectName, habitatName, reply. Não inclua texto adicional, explicações, markdown ou blocos de código."`.
  - **Por quê**: Complementa o `responseFormat` no nível de instrução de prompt, reduzindo a probabilidade de modelos free/reasoning adicionarem texto antes do JSON.
  - **Critério de aceite**: Testes unitários com mock do LLM devem validar que o `@SystemMessage` contém a instrução de JSON-only.

---

## Phase 2: Contexto da Conversa (Correção de Qualidade)

> Resolve o "não entendi" quando o usuário se refere a mensagens anteriores. Custo baixo, impacto alto na experiência.

- [x] **T011** Serializar e passar o histórico de mensagens da sessão Redis para o prompt do LLM.
  - **Arquivo**: `src/main/java/com/treco/dex/api/application/service/ConversationAgent.java` e `ChatOnboardingService.java`
  - **O que fazer**:
    1. Adicionar o parâmetro `@V("chatHistory") String chatHistory` à assinatura de `parseUserMessage`.
    2. No `@SystemMessage`, incluir: `"HISTÓRICO DA CONVERSA ATÉ AGORA:\n{{chatHistory}}"`.
    3. Em `ChatOnboardingService.processMessage()`, serializar `session.getMessages()` para uma string formatada (ex.: `"AI: ...\nUSER: ...\n"`) e passá-la ao agent.
  - **Por quê**: Atualmente cada chamada ao LLM é isolada. O modelo não sabe que já perguntou "É um grampeador?" e o usuário respondeu "Não, é um clips". Com o histórico, o contexto é preservado.
  - **Critério de aceite**: Em testes de integração, uma mensagem como "Não, é outra coisa" após uma pergunta do AI deve resultar em `confirmed = false` no `OnboardingIntent`.

- [x] **T012** Limitar o histórico enviado ao LLM às últimas N mensagens para evitar context overflow.
  - **Arquivo**: `ChatOnboardingService.java`
  - **O que fazer**: Extrair apenas as últimas 10 mensagens da lista `session.getMessages()` antes de serializar para o prompt. Tornar esse limite configurável via `application.yaml` (`app.ai.chat-history-limit: 10`).
  - **Por quê**: Modelos free têm janelas de contexto menores. Enviar todo o histórico de uma conversa longa pode causar erros de `context_length_exceeded`.
  - **Critério de aceite**: A configuração `app.ai.chat-history-limit` deve ser respeitada na serialização do histórico.

---

## Phase 3: Diagnóstico e Observabilidade (Correção de Manutenibilidade)

> Torna o `catch` informativo em vez de silencioso, facilitando depuração futura.

- [x] **T013** Diferenciar tipos de erro no `catch` do `ChatOnboardingService` e logar a causa real.
  - **Arquivo**: `src/main/java/com/treco/dex/api/application/service/ChatOnboardingService.java`
  - **O que fazer**: Substituir o `catch (Exception e)` genérico por blocos específicos:
    - `catch (JsonParseException e)` → log `WARN` com mensagem "LLM returned non-JSON response"
    - `catch (OpenAiHttpException e)` (ou equivalente LangChain4j) → log `ERROR` com "LLM API call failed: rate-limit or provider error"
    - `catch (Exception e)` → log `ERROR` genérico como fallback final
  - **Por quê**: Hoje é impossível saber, pelo log, se o fallback disparou por erro de JSON, timeout, rate-limit ou bug. Logs diferenciados permitem triagem rápida.
  - **Critério de aceite**: Cada tipo de falha deve gerar uma linha de log com nível e mensagem distintos.

- [x] **T014** Adicionar métrica de contagem de fallbacks via Spring Actuator / Micrometer.
  - **Arquivo**: `ChatOnboardingService.java`
  - **O que fazer**: Injetar `MeterRegistry` e incrementar um contador `ai.onboarding.fallback.count` com tags `reason` (json_parse_error, api_error, unknown) sempre que o fallback for acionado.
  - **Por quê**: Permite monitorar em produção a taxa de falha do LLM via Prometheus/Grafana sem necessidade de análise de logs.
  - **Critério de aceite**: A métrica `ai.onboarding.fallback.count` deve estar disponível no endpoint `/actuator/metrics`.

---

## Phase 4: Testes de Validação

> Garante que as correções das phases 1–3 não regridam e funcionam conforme esperado.

- [x] **T015** Atualizar `ChatOnboardingServiceTest.java` para cobrir o cenário de JSON Mode ativo.
  - **O que fazer**: Criar testes que validem:
    1. Quando o mock do LLM retorna um `OnboardingIntent` válido, o fallback NÃO é acionado.
    2. Quando o mock do LLM lança `JsonParseException`, o fallback É acionado com log `WARN`.
    3. Quando o histórico da sessão tem mais de 10 mensagens, apenas as últimas 10 são passadas ao agent.
  - **Critério de aceite**: 100% dos novos cenários passam no `./gradlew test`.

- [x] **T016** Criar teste de integração end-to-end simulando a conversa completa com histórico.
  - **O que fazer**: Usando `@SpringBootTest` com mocks do LLM (via `MockChatLanguageModel` do LangChain4j ou Mockito), simular:
    1. Sessão criada no Redis com histórico de 3 mensagens.
    2. Usuário envia "Não, é um clips de papel".
    3. Validar que `OnboardingIntent.confirmed = false` e `objectName = "clips de papel"`.
  - **Critério de aceite**: Teste passa sem necessidade de conexão real ao OpenRouter.
