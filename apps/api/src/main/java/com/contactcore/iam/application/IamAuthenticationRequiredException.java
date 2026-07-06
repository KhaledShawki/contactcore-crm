// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.shared.api.ApiErrorCode;
import com.contactcore.shared.api.LocalizedApiException;

public class IamAuthenticationRequiredException extends RuntimeException implements LocalizedApiException {
    public IamAuthenticationRequiredException(String message) {
        super(message);
    }

    @Override
    public ApiErrorCode errorCode() {
        return ApiErrorCode.IAM_AUTHENTICATION_REQUIRED;
    }
}
