// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.application;

import com.contactcore.shared.api.ApiErrorCode;
import com.contactcore.shared.api.InvalidRequestException;

public class ConnectorAccessDeniedException extends InvalidRequestException {
    public ConnectorAccessDeniedException(String message, Object... messageArguments) {
        super(ApiErrorCode.CONNECTOR_ACCESS_DENIED, message, messageArguments);
    }
}
