// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

import java.time.Instant;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String locale,
        String path
) {
    public static ApiError of(int status, String error, ApiErrorCode code, String message, String locale, String path) {
        return new ApiError(Instant.now(), status, error, code.name(), message, locale, path);
    }
}
