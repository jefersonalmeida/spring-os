package com.jeferson.os.domain.exception;

public class ApiInvalidArgumentException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ApiInvalidArgumentException(String message) {
        super(message);
    }
}
