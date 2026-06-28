// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.api;

import java.util.List;

public record CurrentUserResponse(
        Long id,
        String username,
        String email,
        String displayName,
        List<String> roles
) {}
