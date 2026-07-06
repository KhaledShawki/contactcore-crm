// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.iam;

import com.contactcore.iam.application.CurrentIamSubject;
import com.contactcore.iam.application.IamSubjectAttributes;
import com.contactcore.iam.domain.IamPrincipalRef;
import com.contactcore.security.application.UserPrincipal;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserPrincipalIamSubjectMapper implements AuthenticationIamSubjectMapper {
    private final SpringAuthorityRoleExtractor roleExtractor;

    public UserPrincipalIamSubjectMapper(SpringAuthorityRoleExtractor roleExtractor) {
        this.roleExtractor = roleExtractor;
    }

    @Override
    public boolean supports(Authentication authentication) {
        return authentication != null && authentication.getPrincipal() instanceof UserPrincipal;
    }

    @Override
    public CurrentIamSubject map(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        List<String> roleCodes = roleExtractor.extract(authentication.getAuthorities());
        return new CurrentIamSubject(
                IamPrincipalRef.user(principal.id()),
                roleCodes,
                IamSubjectAttributes.user(principal.id(), principal.getUsername(), principal.email())
        );
    }
}
