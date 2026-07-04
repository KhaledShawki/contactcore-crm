// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

public class InvalidRequestException extends RuntimeException implements LocalizedApiException {
    private final ApiErrorCode errorCode;
    private final Object[] messageArguments;

    public InvalidRequestException(String message) {
        this(ApiErrorCode.INVALID_REQUEST, message);
    }

    public InvalidRequestException(ApiErrorCode errorCode, String message, Object... messageArguments) {
        super(message);
        this.errorCode = errorCode == null ? ApiErrorCode.INVALID_REQUEST : errorCode;
        this.messageArguments = messageArguments == null ? new Object[0] : messageArguments.clone();
    }

    @Override
    public ApiErrorCode errorCode() {
        return errorCode;
    }

    @Override
    public Object[] messageArguments() {
        return messageArguments.clone();
    }
}
