// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.iam;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.contactcore.iam.application.CurrentIamSubject;
import com.contactcore.iam.application.IamAuthenticationRequiredException;
import com.contactcore.iam.domain.IamPrincipalRef;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class SpringSecurityCurrentIamSubjectResolverTest {
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void rejectsMissingAuthentication() {
        var resolver = new SpringSecurityCurrentIamSubjectResolver(List.of(trueMapperSubject()));

        assertThatThrownBy(resolver::currentSubject)
                .isInstanceOf(IamAuthenticationRequiredException.class)
                .hasMessageContaining("Authentication");
    }

    @Test
    void resolvesSubjectThroughMatchingMapper() {
        CurrentIamSubject expected = CurrentIamSubject.of(IamPrincipalRef.system("test-runner"), List.of("ADMIN"));
        AuthenticationIamSubjectMapper mapper = new AuthenticationIamSubjectMapper() {
            @Override
            public boolean supports(Authentication authentication) {
                return "supported".equals(authentication.getPrincipal());
            }

            @Override
            public CurrentIamSubject map(Authentication authentication) {
                return expected;
            }
        };
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("supported", null, List.of()));

        CurrentIamSubject subject = new SpringSecurityCurrentIamSubjectResolver(List.of(mapper)).currentSubject();

        assertThat(subject).isSameAs(expected);
    }

    @Test
    void rejectsAuthenticatedPrincipalWithoutMapper() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("unsupported", null, List.of()));
        var resolver = new SpringSecurityCurrentIamSubjectResolver(List.of());

        assertThatThrownBy(resolver::currentSubject)
                .isInstanceOf(IamAuthenticationRequiredException.class)
                .hasMessageContaining("cannot be resolved");
    }

    private AuthenticationIamSubjectMapper trueMapperSubject() {
        return new AuthenticationIamSubjectMapper() {
            @Override
            public boolean supports(Authentication authentication) {
                return true;
            }

            @Override
            public CurrentIamSubject map(Authentication authentication) {
                return CurrentIamSubject.of(IamPrincipalRef.system("unused"), List.of());
            }
        };
    }
}
