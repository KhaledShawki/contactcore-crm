// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.localization;

import com.contactcore.profile.domain.UserProfileRepository;
import com.contactcore.security.application.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LocaleContextResolver {
    private final UserProfileRepository profiles;

    public LocaleContextResolver(UserProfileRepository profiles) {
        this.profiles = profiles;
    }

    public LocaleContext resolveForUser(Long userId) {
        if (userId == null) {
            return new LocaleContext(SupportedLocale.DEFAULT);
        }
        return profiles.findByUserId(userId)
                .map(profile -> new LocaleContext(SupportedLocale.normalizeOrDefault(profile.getLocale())))
                .orElseGet(() -> new LocaleContext(SupportedLocale.DEFAULT));
    }

    public LocaleContext resolveForRequest(HttpServletRequest request) {
        LocaleContext fromUser = resolveFromSecurityContext();
        if (fromUser != null) {
            return fromUser;
        }
        return resolveFromAcceptLanguage(request);
    }

    private LocaleContext resolveFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }
        return resolveForUser(principal.id());
    }

    private LocaleContext resolveFromAcceptLanguage(HttpServletRequest request) {
        if (request == null) {
            return new LocaleContext(SupportedLocale.DEFAULT);
        }
        Locale preferred = request.getLocale();
        return new LocaleContext(SupportedLocale.normalizeOrDefault(preferred == null ? null : preferred.toLanguageTag()));
    }
}
