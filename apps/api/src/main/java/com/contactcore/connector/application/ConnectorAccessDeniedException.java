// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.application;

import com.contactcore.shared.api.InvalidRequestException;

public class ConnectorAccessDeniedException extends InvalidRequestException {
    public ConnectorAccessDeniedException(String message) {
        super(message);
    }
}
