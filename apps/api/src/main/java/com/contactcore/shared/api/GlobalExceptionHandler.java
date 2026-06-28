// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

import com.contactcore.storage.scanning.FileScanningUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String PRIMARY_CONTACT_CONSTRAINT = "uq_bp_contact_person_primary";
    private static final String PRIMARY_CONTACT_CONFLICT_MESSAGE =
            "Another contact person is already marked as primary for this business partner. "
                    + "Unselect the current primary contact first, then save this contact as primary.";


    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> notFound(NotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidRequestException.class)
    ResponseEntity<ApiError> invalidRequest(InvalidRequestException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    ResponseEntity<ApiError> rateLimitExceeded(RateLimitExceededException ex, HttpServletRequest request) {
        return build(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> dataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        if (containsMessage(ex, PRIMARY_CONTACT_CONSTRAINT)) {
            return build(HttpStatus.CONFLICT, PRIMARY_CONTACT_CONFLICT_MESSAGE, request);
        }
        return build(HttpStatus.CONFLICT, "The record could not be saved because it conflicts with existing data.", request);
    }

    @ExceptionHandler(FileScanningUnavailableException.class)
    ResponseEntity<ApiError> fileScannerUnavailable(FileScanningUnavailableException ex, HttpServletRequest request) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request);
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    ResponseEntity<ApiError> authentication(RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid username or password.", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

	@ExceptionHandler(Exception.class)
	ResponseEntity<ApiError> unexpected(Exception ex, HttpServletRequest request) {
		LOGGER.error(
				"Unhandled API exception. method={} path={}",
				request.getMethod(),
				request.getRequestURI(),
				ex
		);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error.", request);
	}

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(ApiError.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
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

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
