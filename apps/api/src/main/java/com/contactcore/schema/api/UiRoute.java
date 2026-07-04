// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

public record UiRoute(
        String path,
        String label,
        String labelKey,
        String screenKey
) {}
