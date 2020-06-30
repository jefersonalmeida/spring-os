package com.jeferson.os.domain.exception;

public class ApiModelExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ApiModelExistsException(String message) {
        super(message);
    }
}
