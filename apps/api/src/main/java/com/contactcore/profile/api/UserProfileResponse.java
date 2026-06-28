// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.profile.api;

public record UserProfileResponse(
        Long id,
        Long userId,
        String username,
        String email,
        String displayName,
        String phone,
        String jobTitle,
        String bio,
        String locale,
        String timezone,
        String profileImageUrl
) {}
