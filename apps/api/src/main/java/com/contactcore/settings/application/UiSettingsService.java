// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.settings.application;

import com.contactcore.security.domain.AppUser;
import com.contactcore.security.domain.AppUserRepository;
import com.contactcore.settings.api.UiSettingsResponse;
import com.contactcore.settings.api.UiSettingsWriteRequest;
import com.contactcore.settings.domain.UserUiSettings;
import com.contactcore.settings.domain.UserUiSettingsRepository;
import com.contactcore.shared.api.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UiSettingsService {
    private final UserUiSettingsRepository settings;
    private final AppUserRepository users;

    public UiSettingsService(UserUiSettingsRepository settings, AppUserRepository users) {
        this.settings = settings;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public UiSettingsResponse get(Long userId) {
        return settings.findByUserId(userId).map(UiSettingsService::toResponse).orElseGet(() -> defaultResponse(userId));
    }

    @Transactional
    public UiSettingsResponse update(Long userId, UiSettingsWriteRequest request) {
        NormalizedUiSettingsInput input = UiSettingsNormalizer.normalize(request);
        UserUiSettings entity = settings.findByUserId(userId).orElseGet(() -> new UserUiSettings(findUser(userId)));
        entity.update(
                input.theme(),
                input.textSize(),
                input.density(),
                input.sidebarMode(),
                input.reduceMotion(),
                input.highContrast(),
                input.defaultLandingPage()
        );
        return toResponse(settings.save(entity));
    }

    private AppUser findUser(Long userId) {
        return users.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
    }

    private static UiSettingsResponse defaultResponse(Long userId) {
        return new UiSettingsResponse(null, userId, "ocean", "comfortable", "comfortable", "expanded", false, false, "/dashboard");
    }

    private static UiSettingsResponse toResponse(UserUiSettings entity) {
        return new UiSettingsResponse(
                entity.getId(),
                entity.getUser().getId(),
                entity.getTheme(),
                entity.getTextSize(),
                entity.getDensity(),
                entity.getSidebarMode(),
                entity.isReduceMotion(),
                entity.isHighContrast(),
                entity.getDefaultLandingPage()
        );
    }
}
