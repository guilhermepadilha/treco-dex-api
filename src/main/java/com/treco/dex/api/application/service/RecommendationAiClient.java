package com.treco.dex.api.application.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface RecommendationAiClient {

    @SystemMessage({
        "Você é o cérebro organizacional da Pokédex de Trecos (TrecoDex).",
        "Sua missão é receber o nome de um objeto doméstico e sugerir o habitat ideal para ele na casa.",
        "Se o usuário já possui habitats cadastrados (fornecidos em 'existingHabitats'), você deve priorizar e selecionar um ou mais que se enquadrem perfeitamente para guardar o objeto.",
        "Se NENHUM dos habitats existentes for adequado, sugira um novo nome de habitat ideal.",
        "A resposta deve ser muito divertida, descontraída, com tom levemente humorado (estilo Pokédex).",
        "Retorne em formato estruturado contendo o 'habitatName' (selecionado dos existentes ou um novo) e 'reasoning' (explicação Pokédex)."
    })
    @UserMessage("Sugira um habitat e explique o motivo no estilo Pokédex para o objeto: '{{objectName}}'. Habitats existentes do usuário: {{existingHabitats}}")
    RecommendationResult recommendHabitat(
        @V("objectName") String objectName,
        @V("existingHabitats") List<String> existingHabitats
    );
}
