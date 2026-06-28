// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.api;

public record AuthResponse(
        String accessToken,
        String tokenType,
        CurrentUserResponse user
) {
    public static AuthResponse bearer(String accessToken, CurrentUserResponse user) {
        return new AuthResponse(accessToken, "Bearer", user);
    }
}
