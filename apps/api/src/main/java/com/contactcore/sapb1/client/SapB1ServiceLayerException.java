// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.client;

import com.contactcore.shared.api.InvalidRequestException;

public class SapB1ServiceLayerException extends InvalidRequestException {
    public SapB1ServiceLayerException(String message) {
        super(message);
    }
}
