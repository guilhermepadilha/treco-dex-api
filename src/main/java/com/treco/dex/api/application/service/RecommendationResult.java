package com.treco.dex.api.application.service;

import dev.langchain4j.model.output.structured.Description;

public record RecommendationResult(
    @Description("O nome do habitat recomendado para o objeto doméstico (ex: 'paneleiro', 'gaveta de talheres')")
    String habitatName,

    @Description("Uma descrição amigável, divertida e levemente humorada no estilo Pokédex justificando a recomendação de habitat")
    String reasoning
) {}
