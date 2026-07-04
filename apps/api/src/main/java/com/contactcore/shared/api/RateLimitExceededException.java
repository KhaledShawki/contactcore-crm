// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

public class RateLimitExceededException extends RuntimeException implements LocalizedApiException {
    public RateLimitExceededException(String message) {
        super(message);
    }

    @Override
    public ApiErrorCode errorCode() {
        return ApiErrorCode.RATE_LIMIT_EXCEEDED;
    }
}
