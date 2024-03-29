package com.jeferson.os.api.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import com.jeferson.os.domain.exception.ApiBusinessException;
import com.jeferson.os.domain.exception.ApiInvalidArgumentException;
import com.jeferson.os.domain.exception.ApiModelExistsException;
import com.jeferson.os.domain.exception.ApiModelNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String MESSAGE_DEFAULT
            = "Ocorreu um erro interno inesperado no sistema. Tente novamente e se "
            + "o problema persistir, entre em contato com o administrador do sistema.";

    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        return ResponseEntity.status(status).headers(headers).build();
    }

    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        return handleValidationInternal(ex, headers, status, request, ex.getBindingResult());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        return handleValidationInternal(ex, headers, status, request, ex.getBindingResult());
    }

    private ResponseEntity<Object> handleValidationInternal(
            Exception ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request,
            BindingResult bindingResult
    ) {

        ProblemType problemType = ProblemType.INVALID_DATA;
        String detail = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";

        List<Problem.Object> problemObjects = bindingResult.getAllErrors().stream()
                .map(objectError -> {
                    String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());

                    String name = objectError.getObjectName();

                    if (objectError instanceof FieldError) {
                        name = ((FieldError) objectError).getField();
                    }

                    return Problem.Object.builder()
                            .name(name)
                            .message(message)
                            .build();
                })
                .collect(Collectors.toList());

        Problem problem = createProblemBuilder(status, problemType, detail)
                .message(detail)
                .objects(problemObjects)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @ExceptionHandler(ApiInvalidArgumentException.class)
    public ResponseEntity<Object> handleApiInvalidArgument(ApiInvalidArgumentException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemType problemType = ProblemType.INVALID_DATA;
        String detail = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";

        Problem problem = createProblemBuilder(status, problemType, detail)
                .message(ex.getMessage())
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredential(BadCredentialsException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ProblemType problemType = ProblemType.BAD_CREDENTIAL;
        String detail = "Suas credenciais são inválidas.";

        Problem problem = createProblemBuilder(status, problemType, ex.getMessage())
                .message(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemType problemType = ProblemType.SYSTEM_ERROR;
        String detail = MESSAGE_DEFAULT;

        log.error(ex.getMessage(), ex);

        Problem problem = createProblemBuilder(status, problemType, detail)
                .message(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        ProblemType problemType = ProblemType.RESOURCE_NOT_FOUND;
        String detail = String.format("O recurso %s, que você tentou acessar, é inexistente.",
                ex.getRequestURL());

        Problem problem = createProblemBuilder(status, problemType, detail)
                .message(MESSAGE_DEFAULT)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        if (ex instanceof MethodArgumentTypeMismatchException) {
            return handleMethodArgumentTypeMismatch(
                    (MethodArgumentTypeMismatchException) ex, headers, status, request);
        }

        return super.handleTypeMismatch(ex, headers, status, request);
    }

    private ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        ProblemType problemType = ProblemType.INVALID_PARAMETER;

        String detail = String.format("O parâmetro de URL '%s' recebeu o valor '%s', "
                        + "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.",
                ex.getName(), ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName());

        Problem problem = createProblemBuilder(status, problemType, detail)
                .message(MESSAGE_DEFAULT)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex);

        if (rootCause instanceof InvalidFormatException) {
            return handleInvalidFormat((InvalidFormatException) rootCause, headers, status, request);
        } else if (rootCause instanceof PropertyBindingException) {
            return handlePropertyBinding((PropertyBindingException) rootCause, headers, status, request);
        }

        ProblemType problemType = ProblemType.INCOMPREHENSIBLE_MESSAGE;
        String detail = "O corpo da requisição está inválido. Verifique erro de sintaxe.";

        Problem problem = createProblemBuilder(status, problemType, detail)
                .message(MESSAGE_DEFAULT)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private ResponseEntity<Object> handlePropertyBinding(
            PropertyBindingException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        String path = joinPath(ex.getPath());

        ProblemType problemType = ProblemType.INCOMPREHENSIBLE_MESSAGE;
        String detail = String.format("A propriedade '%s' não existe. "
                + "Corrija ou remova essa propriedade e tente novamente.", path);

        Problem problem = createProblemBuilder(status, problemType, detail)
                .message(MESSAGE_DEFAULT)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private ResponseEntity<Object> handleInvalidFormat(
            InvalidFormatException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        String path = joinPath(ex.getPath());

        ProblemType problemType = ProblemType.INCOMPREHENSIBLE_MESSAGE;
        String detail = String.format("A propriedade '%s' recebeu o valor '%s', "
                        + "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.",
                path, ex.getValue(), ex.getTargetType().getSimpleName());

        Problem problem = createProblemBuilder(status, problemType, detail)
                .message(MESSAGE_DEFAULT)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleEntidadeNaoEncontrada(AccessDeniedException ex, WebRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;
        ProblemType problemType = ProblemType.ACCESS_DENIED;
        String detail = ex.getMessage();

        Problem problem = createProblemBuilder(status, problemType, detail)
                .message(detail)
                .message("Você não possui permissão para executar essa operação.")
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(ApiModelNotFoundException.class)
    public ResponseEntity<?> handleEntidadeNaoEncontrada(ApiModelNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemType problemType = ProblemType.RESOURCE_NOT_FOUND;
        String detail = ex.getMessage();

        Problem problem = createProblemBuilder(status, problemType, detail)
                .message(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(ApiModelExistsException.class)
    public ResponseEntity<?> handleEntidadeEmUso(ApiModelExistsException ex, WebRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        ProblemType problemType = ProblemType.ENTITY_EXIST;
        String detail = ex.getMessage();

        Problem problem = createProblemBuilder(status, problemType, detail)
                .message(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(ApiBusinessException.class)
    public ResponseEntity<?> handleBusiness(ApiBusinessException ex, WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemType problemType = ProblemType.BUSINESS_ERROR;
        String detail = ex.getMessage();

        Problem problem = createProblemBuilder(status, problemType, detail)
                .message(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        if (body == null) {
            body = Problem.builder()
                    .timestamp(OffsetDateTime.now())
                    .title(status.getReasonPhrase())
                    .status(status.value())
                    .message(MESSAGE_DEFAULT)
                    .build();
        } else if (body instanceof String) {
            body = Problem.builder()
                    .timestamp(OffsetDateTime.now())
                    .title((String) body)
                    .status(status.value())
                    .message(MESSAGE_DEFAULT)
                    .build();
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private Problem.ProblemBuilder createProblemBuilder(
            HttpStatus status,
            ProblemType problemType,
            String detail
    ) {
        return Problem.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .type(problemType.getUri())
                .title(problemType.getTitle())
                .detail(detail);
    }

    private String joinPath(List<JsonMappingException.Reference> references) {
        return references.stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining("."));
    }
}
