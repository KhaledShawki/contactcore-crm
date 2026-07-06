// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.iam;

import com.contactcore.iam.application.CurrentIamSubject;
import org.springframework.security.core.Authentication;

public interface AuthenticationIamSubjectMapper {
    boolean supports(Authentication authentication);

    CurrentIamSubject map(Authentication authentication);
}
