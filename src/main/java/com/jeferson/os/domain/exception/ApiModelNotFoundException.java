package com.jeferson.os.domain.exception;

public class ApiModelNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ApiModelNotFoundException(String param) {
        super(String.format("Registro não encontrado %s.", param));
    }
}
