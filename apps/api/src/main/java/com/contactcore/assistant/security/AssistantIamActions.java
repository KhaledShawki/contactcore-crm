// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamActionDescriptor;
import com.contactcore.iam.domain.IamActions;
import java.util.List;

public final class AssistantIamActions {
    public static final String SERVICE = "assistant";

    public static final IamAction ASK = IamActions.of(SERVICE, "Ask");
    public static final IamAction READ_CONVERSATIONS = IamActions.of(SERVICE, "ReadConversations");
    public static final IamAction ARCHIVE_CONVERSATION = IamActions.of(SERVICE, "ArchiveConversation");
    public static final IamAction USE_CRM_TOOLS = IamActions.of(SERVICE, "UseCrmTools");
    public static final IamAction USE_COMMERCIAL_TOOLS = IamActions.of(SERVICE, "UseCommercialTools");
    public static final IamAction USE_CONNECTOR_TOOLS = IamActions.of(SERVICE, "UseConnectorTools");
    public static final IamAction USE_SCHEMA_TOOLS = IamActions.of(SERVICE, "UseSchemaTools");
    public static final IamAction USE_REPORT_TOOLS = IamActions.of(SERVICE, "UseReportTools");

    private static final List<IamActionDescriptor> CATALOG = List.of(
            IamActionDescriptor.read(ASK, "Ask the ContactCore assistant"),
            IamActionDescriptor.read(READ_CONVERSATIONS, "Read the current user's assistant conversations"),
            IamActionDescriptor.write(ARCHIVE_CONVERSATION, "Archive the current user's assistant conversations"),
            IamActionDescriptor.read(USE_CRM_TOOLS, "Use assistant tools that read CRM data"),
            IamActionDescriptor.read(USE_COMMERCIAL_TOOLS, "Use assistant tools that read commercial data"),
            IamActionDescriptor.read(USE_CONNECTOR_TOOLS, "Use assistant tools that read connector data"),
            IamActionDescriptor.read(USE_SCHEMA_TOOLS, "Use assistant tools that read schema metadata"),
            IamActionDescriptor.read(USE_REPORT_TOOLS, "Use assistant tools that read reports and analytics")
    );

    private AssistantIamActions() {}

    public static List<IamActionDescriptor> catalog() {
        return CATALOG;
    }
}
