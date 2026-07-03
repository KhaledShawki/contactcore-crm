// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConnectorLoginRequest(
        @NotNull Long connectorInstanceId,
        @NotBlank @Size(max = 120) String username,
        @NotBlank @Size(max = 255) String password
) {}
