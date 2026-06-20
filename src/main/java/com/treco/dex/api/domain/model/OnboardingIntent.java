package com.treco.dex.api.domain.model;

import dev.langchain4j.model.output.structured.Description;

/**
 * Structured output record extracted by the LLM parser from user natural language messages.
 * Used by the ConversationAgent AI Service to return type-safe parsed intents.
 */
public record OnboardingIntent(

    @Description("true se o usuário confirmou que o nome do objeto está correto, false se rejeitou ou corrigiu")
    Boolean confirmed,

    @Description("O nome correto/confirmado do objeto doméstico (ex: 'mouse', 'cubo mágico', 'copo de vidro')")
    String objectName,

    @Description("O nome do habitat/local físico onde o usuário deseja guardar o objeto (ex: 'mesa do escritório', 'armário da cozinha'). Null se o usuário não mencionou um local.")
    String habitatName,

    @Description("Uma resposta amigável, conversacional e levemente divertida no estilo Pokédex para o usuário, guiando-o para o próximo passo do cadastro")
    String reply
) {}
