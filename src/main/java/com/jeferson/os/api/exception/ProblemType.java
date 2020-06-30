package com.jeferson.os.api.exception;

import lombok.Getter;

@Getter
public enum ProblemType {

    INVALID_DATA("invalid-data", "Dados inválidos"),
    ACCESS_DENIED("access-denied", "Acesso negado"),
    SYSTEM_ERROR("system-error", "Erro de sistema"),
    INVALID_PARAMETER("invalid-parameter", "Parâmetro inválido"),
    INCOMPREHENSIBLE_MESSAGE("incomprehensible-message", "Mensagem incompreensível"),
    RESOURCE_NOT_FOUND("resource-not-found", "Recurso não encontrado"),
    ENTITY_EXIST("entity-exist", "Entidade em uso"),
    BUSINESS_ERROR("business-error", "Violação de regra de negócio");

    private final String title;
    private final String uri;

    ProblemType(String path, String title) {
        this.uri = "http://localhost:8080/" + path;
        this.title = title;
    }
}
