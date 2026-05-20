# Feature Specification: Treco-Dex-API Product Spec

**Feature Branch**: `001-add-treco-dex-api`  
**Created**: 2024-10-01  
**Status**: Draft  
**Input**: User description: "# Treco-Dex-API — Product Specification

## Product Overview

Treco-Dex-API é uma plataforma de organização doméstica inspirada no conceito de uma "Pokédex da vida real", projetada para ajudar usuários a catalogarem, localizarem e organizarem objetos físicos de forma visual, intuitiva e divertida.

O sistema transforma objetos cotidianos em entidades catalogáveis com:
- habitats;
- estados;
- observações;
- histórico;
- descrições contextuais;
- rastreabilidade doméstica.

Treco-Dex-API busca reduzir a frustração causada pela perda recorrente de objetos e pela desorganização doméstica, oferecendo uma experiência baseada em:
- memória visual;
- contexto espacial;
- assistência inteligente;
- humor;
- recuperação rápida de informações.

O sistema deve funcionar como uma camada de memória organizacional doméstica.

---

# Purpose

O propósito do Treco-Dex-API é ajudar usuários a:
- localizar rapidamente objetos;
- devolver itens ao local correto;
- reduzir carga cognitiva relacionada à organização;
- melhorar memória espacial e visual;
- minimizar conflitos domésticos relacionados à desorganização.

O sistema deve transformar organização em uma experiência:
- leve;
- acessível;
- acolhedora;
- divertida;
- visual.

---

# Problem Statement

Usuários frequentemente:
- esquecem onde objetos foram guardados;
- movem itens sem registrá-los mentalmente;
- perdem tempo procurando objetos;
- armazenam itens em locais inconsistentes;
- dependem de outras pessoas para encontrar objetos.

Esse problema é especialmente relevante para:
- pessoas com TDAH;
- usuários neurodivergentes;
- ambientes domésticos compartilhados;
- usuários com rotina corrida;
- usuários com baixa organização espacial.

Ferramentas tradicionais de inventário doméstico geralmente:
- possuem excesso de burocracia;
- exigem manutenção manual complexa;
- não utilizam memória visual;
- não consideram comportamento cotidiano real.

Treco-Dex-API busca resolver esse problema utilizando:
- contexto visual;
- habitats;
- observações;
- organização contextual;
- assistência inteligente;
- workflows simplificados.

---

# Product Goals

## Primary Goals

- Reduzir tempo gasto procurando objetos;
- Facilitar devolução correta de itens;
- Melhorar memória espacial e visual;
- Reduzir frustração doméstica;
- Criar experiência organizacional recorrente e agradável.

---

## Secondary Goals

- Permitir rastreamento contextual de objetos;
- Auxiliar ambientes compartilhados;
- Criar memória organizacional doméstica;
- Possibilitar evolução futura baseada em IA;
- Permitir organização progressiva sem exigir disciplina extrema.

---

# Value Proposition

Treco-Dex-API oferece:
- organização visual;
- recuperação rápida de contexto;
- rastreamento doméstico leve;
- assistência inteligente contextual;
- experiência inspirada em colecionáveis e descoberta.

O sistema não deve se comportar como ferramenta corporativa de produtividade pesada.

---

# Business Context

Treco-Dex-API atua no contexto de:
- organização doméstica;
- memória espacial;
- assistência contextual;
- recuperação de objetos;
- catalogação visual;
- suporte cognitivo leve.

O sistema deve funcionar em ambientes:
- domésticos;
- compartilhados;
- informais;
- dinâmicos;
- parcialmente desorganizados.

---

# Domain Concepts

## Object

Representa qualquer item físico catalogado pelo usuário.

Exemplos:
- canecas;
- carregadores;
- controles remotos;
- ferramentas;
- chaves;
- utensílios.

Objetos possuem:
- identidade;
- contexto;
- habitats;
- estado;
- histórico;
- observações;
- mídia associada.

No escopo deste backend, o conceito de objeto é exposto como `Object`, enquanto a implementação técnica usa `ObjectSpecies` como a entidade de catálogo que mantém metadados, habitat e contexto de estado.

---

## Habitat

Representa o local esperado ou natural de um objeto.

Habitats podem representar:
- gavetas;
- armários;
- caixas;
- prateleiras;
- ambientes;
- regiões organizacionais.

Habitats devem possuir:
- identificação contextual;
- associação visual;
- agrupamento de objetos;
- rastreabilidade.

---

## Observation

Representa um registro contextual relacionado a um objeto.

Observações podem representar:
- último avistamento;
- mudança de local;
- contexto temporal;
- comportamento percebido;
- interações recentes.

---

## Object State

Representa a condição organizacional atual do objeto.

Estados possíveis incluem:
- em habitat;
- fora do habitat;
- desaparecido;
- recentemente avistado;
- em movimentação;
- desconhecido.

---

## Environment

Representa ambientes organizacionais de alto nível.

Exemplos:
- cozinha;
- escritório;
- lavanderia;
- garagem;
- quarto.

Ambientes agrupam habitats relacionados.

---

# Domain Relationships

- Objetos pertencem a habitats;
- Habitats pertencem a ambientes;
- Objetos podem pertencer a ambientes diretamente ou via seu habitat principal;
- Objetos podem possuir múltiplas observações;
- Objetos podem mudar de estado ao longo do tempo;
- Objetos podem possuir múltiplos registros visuais;
- Observações alteram contexto operacional do objeto.

---

# Functional Rules

## Object Registration Rules

O sistema deve permitir:
- cadastro progressivo;
- edição posterior;
- enriquecimento gradual;
- associação visual;
- categorização contextual.

O sistema não deve exigir preenchimento excessivo durante o cadastro inicial.

---

## Habitat Rules

Todo objeto deve poder possuir:
- habitat principal;
- habitats alternativos opcionais;
- contexto espacial;
- associação visual.

Habitats devem representar locais compreensíveis para humanos.

---

## State Rules

O estado de um objeto deve ser derivado de:
- observações;
- movimentações;
- contexto recente;
- ações do usuário.

Mudanças de estado devem ser rastreáveis.

---

## Observation Rules

Observações devem:
- possuir contexto temporal;
- permitir rastreabilidade;
- enriquecer contexto organizacional;
- auxiliar recuperação futura.

---

## Search Rules

O sistema deve permitir busca utilizando:
- nome;
- contexto;
- ambiente;
- categoria;
- estado;
- observações;
- associações visuais.

O sistema deve priorizar:
- velocidade percebida;
- relevância contextual;
- previsibilidade.

---

## Recommendation Rules

O sistema segue uma estratégia híbrida de recomendação e associação de habitats, combinando verdade factual local com inteligência preditiva multimodal:

1. **Estratégia Híbrida de Recomendação**:
   - **Camada Factual Histórica (Prioridade Máxima)**: Antes de acionar qualquer serviço de inteligência artificial, o sistema consulta o banco de dados do usuário buscando se ele já possui um objeto cadastrado com aquele nome (ou alta similaridade). Se encontrar, sugere imediatamente o mesmo habitat associado no histórico, trazendo sua localização real e a foto do local anteriormente cadastrada.
   - **Camada Preditiva de IA (Fallback)**: Caso o objeto seja inédito para o usuário, o sistema aciona um modelo de IA Generativa via LangChain4j para recomendar locais semanticamente apropriados baseados em inteligência geral doméstica. A integração com LLMs deve ser 100% desacoplada e configurável em `application.yaml` (permitindo chave de API, baseUrl e modelId customizados) para suportar tanto APIs comerciais (OpenAI `gpt-4o-mini`) quanto provedores externos e modelos gratuitos via **OpenRouter** (ex: `meta-llama/llama-3-8b-instruct:free`, `google/gemma-2-9b-it:free`). Structured Outputs são garantidos no backend Java mapeando as respostas para Records com `@Description`.
   
2. **Resiliência e Desacoplamento**:
   - Em caso de falha de conexão com a IA, problemas de cota ou ausência de chave de API, o sistema realiza um *graceful fallback* para regras de correspondência léxicas locais (estáticas), mantendo o sistema 100% funcional.

3. **Características das Recomendações**:
   - Devem ser explicáveis e amigáveis, gerando um campo `reasoning` com tom descontraído e levemente humorado.
   - São estritamente opcionais, permitindo revisão ou rejeição pelo usuário.

---

## Intelligent Assistance Rules

O sistema pode oferecer:
- classificação contextual;
- reconhecimento assistido;
- preenchimento parcial automático;
- geração contextual de descrições;
- auxílio organizacional.

Assistência inteligente nunca deve remover controle do usuário.

---

## Humor Rules

O sistema poderá apresentar descrições humorísticas inspiradas em Pokédex.

O humor deve:
- ser leve;
- contextual;
- acolhedor;
- não ofensivo;
- não constrangedor.

Humor nunca deve:
- ridicularizar usuários;
- gerar culpa;
- infantilizar neurodivergência.

---

# User Flows

## Object Registration Flow (Conversational & Interactive)

O cadastro de objetos é projetado como uma conversação em etapas gerenciada por uma máquina de estados (LangGraph-style) persistida temporariamente em cache stateless (Redis):

1. **Início e Identificação**: O usuário escreve o nome do objeto (ex: "escorredor de massa") ou envia uma foto.
2. **Registro de Mídia**: O sistema pergunta ao usuário se ele deseja adicionar uma foto para o objeto:
   - Se **Sim**: O sistema disponibiliza um canal de upload de mídia (`/api/media/upload`) e associa a foto ao objeto temporário.
   - Se **Não**: O sistema deixa o campo de imagem do objeto em branco.
3. **Associação Inteligente de Habitat**:
   - O sistema realiza uma comparação semântica por Embeddings contra todos os habitats já salvos do usuário.
   - Se encontrar um ou mais habitats compatíveis (ex: "Paneleiro"): O sistema pergunta: *"O [objeto] costuma ficar no [Paneleiro]?"*.
     - Se o usuário responder **Sim**: A associação é gravada e o fluxo finaliza com sucesso.
     - Se o usuário responder **Não**: O sistema prossegue para a criação de um novo habitat.
   - Se não encontrar nenhum habitat compatível no banco: O sistema prossegue automaticamente para a criação de um novo habitat.
4. **Criação de Novo Habitat**:
   - O sistema pergunta: *"Onde é o habitat desse objeto então?"*.
   - O usuário responde o nome do local (ex: "segunda gaveta embaixo da máquina de lavar").
   - O sistema pergunta se o usuário possui uma foto desse novo local:
     - Se **Sim**: O sistema realiza o upload e associa a imagem de contexto ao novo habitat.
     - Se **Não**: O habitat é criado sem foto associada.
5. **Finalização**: O sistema persiste o novo habitat (se aplicável), vincula a `ObjectSpecies` a ele e gera a descrição estilo Pokédex.

---

## Object Recovery Flow (Multimodal Visual Search)

A busca e recuperação física de itens suporta um fluxo de visão computacional inovador e dinâmico:

1. **Input Multimodal**: O usuário envia uma foto em vez de escrever um termo de busca.
2. **Análise por Visão Computacional**: O backend utiliza um modelo de visão computacional (multimodal) configurável via LangChain4j (como o OpenAI `gpt-4o-mini` ou outros modelos visuais de baixo custo/gratuitos via **OpenRouter**) para identificar o objeto principal contido na imagem.
3. **Busca Factual no Catálogo**: O sistema realiza imediatamente uma busca no banco de dados local do usuário pelo nome identificado.
4. **Resolução de Fluxo**:
   - **Caso de Sucesso (Item Encontrado)**: Se o objeto já estiver cadastrado, o sistema retorna diretamente a **Treco-Dex Entry** com a descrição contextual divertida da localização, o nome do habitat real e a foto do local previamente cadastrado. O fluxo termina.
   - **Caso de Onboarding (Item Inédito)**: Se o objeto não for encontrado, o sistema salva a foto original e o nome sugerido pela IA em cache de conversação (Redis), muda o estado da sessão e pergunta ativamente ao usuário: *"Não encontrei esse ser no meu catálogo. Isso é um [escorredor de massa]?"*.
     - Se o usuário responder **Sim**: Inicia o processo de cadastro do objeto usando a foto originalmente enviada e o nome sugerido.
     - Se o usuário responder **Não**: Pergunta o que é o item. O usuário fornece o nome correto e o sistema inicia o cadastro usando a foto original e o novo nome.

---

## Reorganization Flow

Fluxo esperado:
1. Usuário identifica item fora do habitat;
2. Sistema apresenta habitat esperado;
3. Usuário reorganiza objeto;
4. Estado organizacional é atualizado.

---

## Progressive Enrichment Flow

Fluxo esperado:
1. Usuário registra objeto rapidamente;
2. Sistema permite enriquecimento posterior;
3. Contexto evolui ao longo do uso.

---

# Expected Capabilities

## Contextual Matching

O sistema deve suportar:
- associação contextual;
- agrupamentos inteligentes;
- matching semântico futuro;
- reconhecimento contextual assistido.

---

## Intelligent Recommendations

O sistema poderá oferecer:
- sugestões organizacionais;
- agrupamento contextual;
- recomendações de habitats;
- organização baseada em comportamento observado.

---

## Contextual Recovery

O sistema deve auxiliar usuários a:
- recuperar memória espacial;
- entender contexto organizacional;
- localizar objetos rapidamente.

---

## Intelligent Workflows

O sistema poderá evoluir para:
- workflows assistidos;
- organização contextual automática;
- enriquecimento inteligente;
- rastreamento contextual avançado.

---

# Functional Quality Criteria

## Predictability

O sistema deve possuir comportamento previsível e consistente.

Usuários devem compreender:
- estados;
- mudanças;
- recomendações;
- contexto apresentado.

---

## Explainability

Sugestões automáticas devem ser compreensíveis.

O sistema deve evitar comportamento excessivamente opaco.

---

## Traceability

Mudanças relevantes devem ser rastreáveis.

O sistema deve permitir entendimento contextual de:
- movimentações;
- estados;
- observações;
- histórico organizacional.

---

## Consistency

O sistema deve manter consistência:
- visual;
- contextual;
- organizacional;
- comportamental.

---

## Safety

O sistema deve evitar:
- comportamento ofensivo;
- automações imprevisíveis;
- alterações não explicáveis;
- perda silenciosa de contexto.

---

# Functional Security

## User Protection

O sistema deve proteger usuários contra:
- perda acidental de contexto;
- comportamento inesperado;
- alterações irreversíveis não explícitas.

---

## Moderation

Conteúdo gerado automaticamente deve respeitar:
- segurança contextual;
- neutralidade;
- ausência de linguagem ofensiva.

---

## Controlled Automation

Automações devem:
- ser revisáveis;
- possuir supervisão humana;
- ser transparentes;
- possuir comportamento previsível.

---

# MVP Scope

O MVP deve incluir:
- cadastro de objetos;
- habitats;
- observações básicas;
- estados organizacionais;
- associação visual;
- busca contextual;
- histórico básico;
- descrições contextuais;
- rastreabilidade básica.

---

# Future Evolution

Evoluções futuras poderão incluir:
- reconhecimento avançado;
- classificação multimodal;
- embeddings;
- workflows inteligentes;
- rastreamento contextual avançado;
- múltiplos usuários;
- automações organizacionais;
- recomendações inteligentes avançadas;
- integração com dispositivos externos;
- memória contextual avançada.

---

# Out of Scope

Não fazem parte deste escopo:
- automação residencial completa;
- substituição de inventários corporativos;
- vigilância doméstica;
- monitoramento invasivo;
- reconhecimento perfeito obrigatório;
- dependência total de IA;
- gamificação competitiva agressiva."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Register and Catalog Objects (Priority: P1)

As a user with disorganized household items, I want to register objects in the system so that I can build a catalog of my belongings for easy retrieval.

**Why this priority**: This is the core functionality that enables all other features, providing the foundation for organization.

**Independent Test**: Can be fully tested by registering objects and verifying they appear in the catalog, delivering value through basic inventory creation.

**Acceptance Scenarios**:

1. **Given** a user has an object to register, **When** they provide basic information (name, habitat), **Then** the object is added to the catalog with a default state.
2. **Given** an object is registered, **When** the user adds visual context (photo), **Then** the object entry is enriched with visual association.
3. **Given** a registered object, **When** the user edits details later, **Then** the changes are saved and reflected in the catalog.

---

### User Story 2 - Locate Objects Quickly (Priority: P1)

As a user searching for a lost item, I want to search the catalog using various criteria so that I can find objects rapidly.

**Why this priority**: Addresses the primary pain point of finding lost objects, providing immediate value.

**Independent Test**: Can be fully tested by searching for objects and verifying correct results are returned, delivering value through quick retrieval.

**Acceptance Scenarios**:

1. **Given** objects are in the catalog, **When** the user searches by name, **Then** relevant objects are displayed with their habitats.
2. **Given** an object has observations, **When** the user searches by context, **Then** the last known location and state are shown.
3. **Given** a search query, **When** results are returned, **Then** they are ordered by relevance and include visual cues.

---

### User Story 3 - Track Object States and Movements (Priority: P2)

As a user reorganizing my space, I want to track object states and add observations so that I can maintain accurate organizational context.

**Why this priority**: Supports ongoing organization and helps prevent future disorganization.

**Independent Test**: Can be fully tested by adding observations and verifying state changes are recorded, delivering value through movement tracking.

**Acceptance Scenarios**:

1. **Given** an object is moved, **When** the user adds an observation, **Then** the state changes to reflect the new context.
2. **Given** an object is in its habitat, **When** it's found elsewhere, **Then** the state updates to "out of habitat" with timestamp.
3. **Given** multiple observations, **When** viewing history, **Then** all changes are traceable with dates and contexts.

---

### User Story 4 - Receive Organizational Assistance (Priority: P3)

As a user building better habits, I want intelligent recommendations for habitats and groupings so that I can improve my organization progressively.

**Why this priority**: Enhances the system with smart features without being essential for basic functionality.

**Independent Test**: Can be fully tested by triggering recommendations and verifying they are helpful and optional, delivering value through assisted organization.

**Acceptance Scenarios**:

1. **Given** objects without habitats, **When** the system suggests habitats, **Then** suggestions are based on similar objects and are explainable.
2. **Given** usage patterns, **When** recommendations are shown, **Then** they can be accepted, rejected, or ignored without affecting core functionality.
3. **Given** a new object, **When** registering, **Then** contextual suggestions appear but are not required.

---

### Edge Cases

- What happens when an object is registered without a habitat? (System allows it but suggests assignment)
- How does system handle objects with multiple possible habitats? (Allows primary and alternative habitats)
- What happens when search returns no results? (Shows helpful suggestions or related objects)
- How does system handle duplicate object names? (Allows duplicates with context differentiation)
- What happens when user adds conflicting observations? (Maintains history but flags for review)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow users to register objects with basic information (name, description)
- **FR-002**: System MUST support association of objects with habitats (primary and alternative)
- **FR-003**: System MUST enable visual association through photos or media
- **FR-004**: System MUST track object states (in habitat, out of habitat, missing, etc.)
- **FR-005**: System MUST allow adding observations with temporal context
- **FR-006**: System MUST provide search functionality by name, habitat, state, and observations
- **FR-007**: System MUST support high-level environments that group related habitats and improve catalog context
- **FR-008**: System MUST support progressive enrichment of object information
- **FR-009**: System MUST provide conversational suggestions and multimodal visual identification for objects and habitats using a hybrid factual-DB/LLM strategy (Redis session + configurable Vision/Chat model providers like OpenRouter or OpenAI via LangChain4j)
- **FR-010**: System MUST require authenticated access for object, habitat, observation, and search operations

### Key Entities *(include if feature involves data)*

- **Object**: Represents physical items with identity, habitats, states, observations, and media; internally implemented as `ObjectSpecies` catalog entries
- **Habitat**: Represents physical locations or containers with contextual identification and visual association
- **Observation**: Represents contextual records of object sightings, movements, or changes with timestamps
- **Object State**: Represents current organizational condition derived from observations and user actions
- **Environment**: Represents high-level organizational areas grouping related habitats
- **User Account**: Represents authenticated users of the backend and access control context

## Success Criteria *(mandatory)*

## Measurable Outcomes

- **SC-001**: Users can register a new object in under 30 seconds with minimal required information.
- **SC-002**: Users can locate any registered object within 10 seconds of searching, measured by integration tests against representative catalog and habitat data.
- **SC-003**: System maintains 99% accuracy in object state tracking based on user observations, validated by automated state transition tests that compare expected state against observed activity.
- **SC-004**: System provides relevant search results for 90% of queries within 2 seconds, measured by end-to-end search test cases covering name, habitat, state, and observation filters.

These success criteria must be supported by integration-level validation and are associated with tasks `T049` and `T050`.

## Assumptions

- Users have access to mobile devices or computers for registration and search
- Basic internet connectivity is available for cloud synchronization
- Users are willing to provide minimal information initially with progressive enrichment
- Visual media (photos) can be captured using device cameras
- System will start with single-user support, expanding to multi-user later
- Authentication is required for backend access; multi-user sharing is a future evolution beyond the MVP.
- English and Portuguese language support is sufficient for initial release
- No integration with external smart home devices required for MVP