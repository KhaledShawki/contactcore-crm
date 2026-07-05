// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.security;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamActionDescriptor;
import com.contactcore.iam.domain.IamActions;
import java.util.List;

public final class ConnectorIamActions {
    public static final String SERVICE = "connector";

    public static final IamAction READ = IamActions.of(SERVICE, "ReadConnector");
    public static final IamAction CONFIGURE = IamActions.of(SERVICE, "ConfigureConnector");
    public static final IamAction START_SYNC = IamActions.of(SERVICE, "StartSync");

    private static final List<IamActionDescriptor> CATALOG = List.of(
            IamActionDescriptor.read(READ, "Read connector configuration and status"),
            IamActionDescriptor.admin(CONFIGURE, "Configure connector settings"),
            IamActionDescriptor.system(START_SYNC, "Start connector synchronization")
    );

    private ConnectorIamActions() {}

    public static List<IamActionDescriptor> catalog() {
        return CATALOG;
    }
}
