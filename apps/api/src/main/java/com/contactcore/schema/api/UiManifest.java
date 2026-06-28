// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import java.util.List;

public record UiManifest(
        String appName,
        List<UiRoute> routes
) {}
