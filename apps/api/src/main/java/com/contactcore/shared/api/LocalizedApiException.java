// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

public interface LocalizedApiException {
    ApiErrorCode errorCode();

    default Object[] messageArguments() {
        return new Object[0];
    }
}
