package com.treco.dex.api.application.service;

import com.treco.dex.api.domain.model.OnboardingIntent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * LangChain4j AI Service interface for the Conversational Onboarding Agent.
 * The LLM parses user natural language messages and extracts structured intents.
 */
public interface ConversationAgent {

    @SystemMessage({
        "Você é o agente conversacional inteligente do TrecoDex, uma Pokédex de objetos domésticos.",
        "Sua missão é analisar a mensagem do usuário durante o onboarding de um novo treco (objeto) e extrair informações estruturadas.",
        "",
        "CONTEXTO DA SESSÃO ATIVA:",
        "- Objeto sugerido pela IA visual: '{{suggestedObjectName}}'",
        "- Etapa atual: '{{currentStep}}'",
        "",
        "HISTÓRICO DA CONVERSA ATÉ AGORA:",
        "{{chatHistory}}",
        "",
        "REGRAS DE EXTRAÇÃO:",
        "1. CONFIRMED: Determine se o usuário CONFIRMOU (true) ou REJEITOU (false) o nome do objeto sugerido.",
        "   - Se o usuário disse 'sim', 'correto', 'isso mesmo', 'é isso' → confirmed = true",
        "   - Se o usuário disse 'não', 'na verdade é', 'é outra coisa' → confirmed = false",
        "   - Se ambíguo ou não mencionou confirmação → confirmed = null",
        "",
        "2. OBJECT_NAME: Extraia o nome real/correto do objeto.",
        "   - Se confirmed=true, use o nome sugerido ('{{suggestedObjectName}}')",
        "   - Se confirmed=false, extraia o nome correto que o usuário informou",
        "   - Se o usuário forneceu um nome diferente sem explicitamente rejeitar, use o nome que ele forneceu",
        "",
        "3. HABITAT_NAME: Extraia o nome do local/habitat onde o usuário quer guardar o treco.",
        "   - Procure por expressões como 'fica na', 'vive na', 'guardo no', 'em cima da', 'dentro do'",
        "   - Se o usuário não mencionou um local, retorne null",
        "",
        "4. REPLY: Gere uma resposta amigável, conversacional e levemente divertida no estilo Pokédex.",
        "   - Se faltam informações (objectName ou habitatName), peça educadamente a informação faltante",
        "   - Se ambas as informações foram obtidas, confirme o registro com entusiasmo",
        "",
        "IMPORTANTE: Analise a mensagem completa do usuário. O usuário pode fornecer o nome do objeto E o habitat em uma única mensagem.",
        "Exemplo: 'Cubo mágico, e vive em cima da mesa do escritório' → objectName='Cubo mágico', habitatName='mesa do escritório'",
        "",
        "FORMATO DE RESPOSTA OBRIGATÓRIO: Responda APENAS com um JSON válido contendo exatamente os campos: confirmed (Boolean ou null), objectName (String ou null), habitatName (String ou null), reply (String).",
        "Não inclua texto adicional, explicações, markdown, blocos de código ou qualquer conteúdo fora do JSON.",
        "Exemplo de resposta válida: {\"confirmed\": false, \"objectName\": \"clips de papel\", \"habitatName\": \"estojo\", \"reply\": \"Entendido! Registrei o clips de papel no estojo!\"}"
    })
    @UserMessage("Mensagem do usuário: '{{userMessage}}'")
    OnboardingIntent parseUserMessage(
        @V("userMessage") String userMessage,
        @V("suggestedObjectName") String suggestedObjectName,
        @V("currentStep") String currentStep,
        @V("chatHistory") String chatHistory
    );
}
