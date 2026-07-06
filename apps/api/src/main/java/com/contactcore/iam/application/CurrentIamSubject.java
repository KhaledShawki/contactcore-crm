// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamPrincipalRef;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public record CurrentIamSubject(
        IamPrincipalRef principal,
        List<String> roleCodes,
        IamSubjectAttributes attributes
) {
    public CurrentIamSubject {
        principal = Objects.requireNonNull(principal, "principal must not be null");
        roleCodes = roleCodes == null ? List.of() : List.copyOf(roleCodes);
        attributes = attributes == null ? IamSubjectAttributes.empty() : attributes;
    }

    public static CurrentIamSubject of(IamPrincipalRef principal, Collection<String> roleCodes) {
        return new CurrentIamSubject(principal, roleCodes == null ? List.of() : List.copyOf(roleCodes), IamSubjectAttributes.empty());
    }

    public static CurrentIamSubject user(Long userId, String username, String email, Collection<String> roleCodes) {
        return new CurrentIamSubject(
                IamPrincipalRef.user(userId),
                roleCodes == null ? List.of() : List.copyOf(roleCodes),
                IamSubjectAttributes.user(userId, username, email)
        );
    }

    public boolean hasRole(String roleCode) {
        if (roleCode == null || roleCode.isBlank()) {
            return false;
        }
        return roleCodes.contains(roleCode.trim().toUpperCase(java.util.Locale.ROOT));
    }
}
