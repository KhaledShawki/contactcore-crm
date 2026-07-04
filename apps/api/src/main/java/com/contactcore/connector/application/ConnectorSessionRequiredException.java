// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.application;

import com.contactcore.shared.api.ApiErrorCode;
import com.contactcore.shared.api.InvalidRequestException;

public class ConnectorSessionRequiredException extends InvalidRequestException {
    public ConnectorSessionRequiredException(String message) {
        super(ApiErrorCode.CONNECTOR_SESSION_REQUIRED, message);
    }
}
