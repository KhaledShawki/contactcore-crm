// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

public class NotFoundException extends RuntimeException implements LocalizedApiException {
    private final ApiErrorCode errorCode;
    private final Object[] messageArguments;

    public NotFoundException(String message) {
        this(ApiErrorCode.NOT_FOUND, message);
    }

    public NotFoundException(ApiErrorCode errorCode, String message, Object... messageArguments) {
        super(message);
        this.errorCode = errorCode == null ? ApiErrorCode.NOT_FOUND : errorCode;
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
