# 📱 TrecoDex API - Backend Platform

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](#)
[![Java Version](https://img.shields.io/badge/java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/spring--boot-3.2.3-green.svg)](https://spring.io/projects/spring-boot)
[![Linter](https://img.shields.io/badge/checkstyle-passing-success.svg)](#)

O **TrecoDex API** é o cérebro backend da aplicação **TrecoDex** (a "Pokédex" para organização e catalogação de objetos domésticos). Projetado com base nos princípios do *Domain-Driven Design (DDD)* e em uma arquitetura moderna e reativa, o sistema ajuda usuários a catalogar seus trecos, monitorar estados, gerenciar habitats de forma factual e inteligente por meio de inteligência artificial multimodal (visão e linguagem).

---

## 🌟 Recursos Principais

*   🔐 **Autenticação JWT Segura**: Cadastro e controle de acesso individualizados.
*   📦 **Catálogo de Trecos (Object Species)**: Cadastro de propriedades físicas, dimensões e associação hierárquica de habitats.
*   🏡 **Gestão de Habitats e Ambientes**: Estruturação espacial da residência (cozinhas, quartos, gavetas, armários).
*   🔍 **Busca Semântica & Contextual**: Localização instantânea de objetos por termos e filtros inteligentes.
*   📈 **Histórico de Estados & Movimentações**: Registro cronológico de observações e transições físicas dos objetos.
*   🤖 **Recomendação Híbrida de Habitats (T033a)**: Priorização de dados factuais (banco de dados) e enriquecimento inteligente por IA via LangChain4j se o objeto for novo, adaptável a habitats existentes do usuário.
*   📷 **Busca Visual Multimodal (T033b)**: Reconhecimento automático de objetos a partir de imagens com ativação de máquina de estados conversacional temporária persistida em **Redis**.
*   ⚡ **Processamento de Mídia Assíncrono**: Pipeline robusto de upload de imagens integrado com mensageria assíncrona **Apache Kafka**.

---

## 🛠️ Stack Tecnológica

*   **Linguagem & Framework**: Java 21 / Spring Boot 3.2.3
*   **Banco de Dados**: PostgreSQL (Persistência Relacional) + Flyway Migrations
*   **Cache & Estado Conversacional**: Redis (Máquina de Estados de Onboarding Stateless)
*   **Mensageria**: Apache Kafka
*   **Orquestração de LLMs**: LangChain4j (Totalmente desacoplado e compatível com OpenAI e OpenRouter via propriedades Spring)
*   **Controle de Qualidade**: Gradle Checkstyle (Static Analysis)
*   **Ambiente de Testes**: Testcontainers (Banco de dados e serviços reais em containers descartáveis para testes de integração)

---

## 📁 Estrutura do Projeto

O código-fonte segue as convenções do DDD, garantindo que a lógica de negócios e o domínio permaneçam isolados das dependências de infraestrutura e provedores externos (como modelos de IA):

```text
src/main/java/com/treco/dex/api/
├── api/                  # Camada de Apresentação (Controllers, DTOs, Segurança, Configs)
├── application/          # Camada de Aplicação (Serviços e Interfaces de Comunicação)
├── domain/               # Camada de Domínio (Entidades de Negócio e Repositórios Abstratos)
└── infrastructure/       # Camada de Infraestrutura (Mensageria, Banco de Dados, Telemetria)
```

---

## ⚙️ Variáveis de Ambiente e Configuração

O sistema de IA foi desenhado para ser **100% desacoplado**, permitindo a alternância de modelos através das propriedades do [application.yaml](src/main/resources/application.yaml):

```yaml
app:
  ai:
    base-url: ${AI_BASE_URL:https://openrouter.ai/api/v1}
    api-key: ${AI_API_KEY:demo}
    model-name: ${AI_MODEL_NAME:meta-llama/llama-3-8b-instruct:free}
    http-referer: ${AI_HTTP_REFERER:https://trecodex.com}
    x-title: ${AI_X_TITLE:TrecoDex API}
```

Para usar **OpenAI**, configure:
*   `AI_BASE_URL`: `https://api.openai.com/v1`
*   `AI_API_KEY`: Sua chave de API da OpenAI
*   `AI_MODEL_NAME`: `gpt-4o-mini`

Para usar **OpenRouter** com modelos gratuitos:
*   `AI_BASE_URL`: `https://openrouter.ai/api/v1`
*   `AI_API_KEY`: Sua chave do OpenRouter (ou `demo` para modo stub offline de testes)
*   `AI_MODEL_NAME`: O modelo de sua preferência (ex: `meta-llama/llama-3-8b-instruct:free`)

---

## 🚀 Como Iniciar Localmente

### 1. Iniciar Runtimes (Postgres, Redis, Kafka)
Use o Docker Compose configurado para subir todos os containers necessários em segundo plano:
```bash
docker-compose -f docker/docker-compose.yml up -d
```

### 2. Compilar e Iniciar a Aplicação
Compile o projeto e inicie o Spring Boot:
```bash
./gradlew bootRun
```
A API estará disponível em `http://localhost:8080/api`.

---

## 🧪 Como Executar os Testes e Linter

Garantimos a confiabilidade do código por meio de testes unitários, testes de integração de ponta a ponta e análise de estilo:

### Executar Testes Automatizados (com Testcontainers)
```bash
./gradlew test
```

### Executar Análise de Estilo (Checkstyle)
```bash
./gradlew checkstyleMain
```
Os relatórios detalhados do linter estarão disponíveis em `build/reports/checkstyle/main.html`.

---

## 📌 Principais Endpoints da API

A documentação OpenAPI/Swagger completa das rotas está descrita no arquivo [api-contract.md](specs/001-treco-dex-api-product-spec/contracts/api-contract.md).

*   `POST /api/auth/register` - Cadastro de usuário
*   `POST /api/auth/login` - Login e geração de token JWT
*   `POST /api/objects` - Cadastro de trecos
*   `POST /api/objects/recommend` - Recomendação híbrida inteligente de habitat ideal para o treco
*   `POST /api/objects/visual-search` - Detecção de objeto e checagem factual por envio de imagem
*   `GET /api/search` - Busca semântica e filtrada de objetos no catálogo

---

## License

This project is licensed under the Apache License 2.0.

Copyright (c) 2026 Guilherme Santos Padilha