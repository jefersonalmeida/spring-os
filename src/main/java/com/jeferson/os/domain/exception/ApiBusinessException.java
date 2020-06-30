package com.jeferson.os.domain.exception;

public class ApiBusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ApiBusinessException(String mensagem) {
		super(mensagem);
	}
	
	public ApiBusinessException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
	
}
