// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.iam;

import com.contactcore.iam.application.CurrentIamSubject;
import com.contactcore.iam.application.CurrentIamSubjectResolver;
import com.contactcore.iam.application.IamAuthenticationRequiredException;
import java.util.List;
import java.util.Objects;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityCurrentIamSubjectResolver implements CurrentIamSubjectResolver {
    private final List<AuthenticationIamSubjectMapper> mappers;

    public SpringSecurityCurrentIamSubjectResolver(List<AuthenticationIamSubjectMapper> mappers) {
        this.mappers = mappers == null ? List.of() : mappers.stream()
                .map(mapper -> Objects.requireNonNull(mapper, "mapper must not be null"))
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .toList();
    }

    @Override
    public CurrentIamSubject currentSubject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(authentication)) {
            throw new IamAuthenticationRequiredException("Authentication is required for this operation.");
        }
        return mappers.stream()
                .filter(mapper -> mapper.supports(authentication))
                .findFirst()
                .map(mapper -> mapper.map(authentication))
                .orElseThrow(() -> new IamAuthenticationRequiredException("The authenticated principal cannot be resolved as an IAM subject."));
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
