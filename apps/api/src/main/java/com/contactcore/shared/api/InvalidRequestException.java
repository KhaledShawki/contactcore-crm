// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
