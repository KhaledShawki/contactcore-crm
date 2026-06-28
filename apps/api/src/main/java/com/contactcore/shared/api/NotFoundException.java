// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
