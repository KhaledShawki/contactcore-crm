// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.planning;

import com.contactcore.shared.api.InvalidRequestException;

public class AssistantPlanValidationException extends InvalidRequestException {
    public AssistantPlanValidationException(String message) {
        super(message);
    }
}
