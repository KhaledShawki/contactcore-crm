// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamActionDescriptor;
import com.contactcore.iam.domain.IamActions;
import java.util.List;

public final class AssistantIamActions {
    public static final String SERVICE = "assistant";

    public static final IamAction ASK = IamActions.of(SERVICE, "Ask");
    public static final IamAction USE_COMMERCIAL_TOOLS = IamActions.of(SERVICE, "UseCommercialTools");

    private static final List<IamActionDescriptor> CATALOG = List.of(
            IamActionDescriptor.read(ASK, "Ask the ContactCore assistant"),
            IamActionDescriptor.read(USE_COMMERCIAL_TOOLS, "Use assistant tools that read commercial data")
    );

    private AssistantIamActions() {}

    public static List<IamActionDescriptor> catalog() {
        return CATALOG;
    }
}
