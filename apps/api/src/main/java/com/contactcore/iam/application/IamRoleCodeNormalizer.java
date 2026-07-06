// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class IamRoleCodeNormalizer {
    private static final String SPRING_ROLE_PREFIX = "ROLE_";

    public List<String> normalize(Collection<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        roleCodes.stream()
                .map(this::normalizeOne)
                .flatMap(Optional::stream)
                .forEach(normalized::add);
        return List.copyOf(normalized);
    }

    public Optional<String> normalizeOne(String roleCode) {
        if (roleCode == null || roleCode.isBlank()) {
            return Optional.empty();
        }
        String normalized = Objects.requireNonNull(roleCode).trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith(SPRING_ROLE_PREFIX)) {
            normalized = normalized.substring(SPRING_ROLE_PREFIX.length());
        }
        if (normalized.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(normalized);
    }
}
