// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
