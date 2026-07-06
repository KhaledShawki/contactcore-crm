// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

import com.contactcore.iam.application.IamAccessDeniedException;
import com.contactcore.iam.application.IamAuthenticationRequiredException;
import com.contactcore.shared.localization.LocaleContext;
import com.contactcore.shared.localization.LocaleContextResolver;
import com.contactcore.shared.localization.LocalizedMessageService;
import com.contactcore.storage.scanning.FileScanningUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String PRIMARY_CONTACT_CONSTRAINT = "uq_bp_contact_person_primary";

    private final LocaleContextResolver localeContextResolver;
    private final LocalizedMessageService messages;

    public GlobalExceptionHandler(LocaleContextResolver localeContextResolver, LocalizedMessageService messages) {
        this.localeContextResolver = localeContextResolver;
        this.messages = messages;
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> notFound(NotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(InvalidRequestException.class)
    ResponseEntity<ApiError> invalidRequest(InvalidRequestException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    ResponseEntity<ApiError> rateLimitExceeded(RateLimitExceededException ex, HttpServletRequest request) {
        return build(HttpStatus.TOO_MANY_REQUESTS, ex, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> dataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        ApiErrorCode code = ApiErrorCode.DATA_INTEGRITY_CONFLICT;
        String fallback = containsMessage(ex, PRIMARY_CONTACT_CONSTRAINT)
                ? "Another contact person is already marked as primary for this business partner. Unselect the current primary contact first, then save this contact as primary."
                : "The record could not be saved because it conflicts with existing data.";
        return build(HttpStatus.CONFLICT, code, fallback, request);
    }

    @ExceptionHandler(FileScanningUnavailableException.class)
    ResponseEntity<ApiError> fileScannerUnavailable(FileScanningUnavailableException ex, HttpServletRequest request) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, ApiErrorCode.FILE_SCANNING_UNAVAILABLE, ex.getMessage(), request);
    }

    @ExceptionHandler(IamAuthenticationRequiredException.class)
    ResponseEntity<ApiError> iamAuthenticationRequired(IamAuthenticationRequiredException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex, request);
    }

    @ExceptionHandler(IamAccessDeniedException.class)
    ResponseEntity<ApiError> iamAccessDenied(IamAccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, ex, request);
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    ResponseEntity<ApiError> authentication(RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ApiErrorCode.AUTH_INVALID_CREDENTIALS, "Invalid username or password.", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        LocaleContext locale = localeContextResolver.resolveForRequest(request);
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> formatFieldError(locale, error))
                .collect(Collectors.joining("; "));
        String resolved = message.isBlank()
                ? messages.message(locale, key(ApiErrorCode.VALIDATION_FAILED), "Validation failed.")
                : message;
        return build(HttpStatus.BAD_REQUEST, ApiErrorCode.VALIDATION_FAILED, resolved, request, locale);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(Exception ex, HttpServletRequest request) {
        LOGGER.error(
                "Unhandled API exception. method={} path={}",
                request.getMethod(),
                request.getRequestURI(),
                ex
        );
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorCode.UNEXPECTED_SERVER_ERROR, "Unexpected server error.", request);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, LocalizedApiException exception, HttpServletRequest request) {
        Throwable throwable = exception instanceof Throwable value ? value : null;
        String fallback = throwable == null ? status.getReasonPhrase() : throwable.getMessage();
        return build(status, exception.errorCode(), fallback, request, exception.messageArguments());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, ApiErrorCode code, String fallback, HttpServletRequest request, Object... args) {
        LocaleContext locale = localeContextResolver.resolveForRequest(request);
        return build(status, code, localized(locale, code, fallback, args), request, locale);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, ApiErrorCode code, String message, HttpServletRequest request, LocaleContext locale) {
        return ResponseEntity.status(status)
                .header("Content-Language", locale.tag())
                .body(ApiError.of(status.value(), status.getReasonPhrase(), code, message, locale.tag(), request.getRequestURI()));
    }

    private String localized(LocaleContext locale, ApiErrorCode code, String fallback, Object... args) {
        return messages.message(locale, key(code), fallback, args);
    }

    private String key(ApiErrorCode code) {
        return "api.error." + code.name();
    }

    private boolean containsMessage(Throwable exception, String value) {
        Throwable current = exception;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.contains(value)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private String formatFieldError(LocaleContext locale, FieldError error) {
        String field = messages.message(locale, "validation.field." + error.getField(), error.getField());
        String message = error.getDefaultMessage();
        return field + ": " + message;
    }
}
