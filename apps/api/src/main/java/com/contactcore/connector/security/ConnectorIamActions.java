// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.security;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamActionDescriptor;
import com.contactcore.iam.domain.IamActions;
import java.util.List;

public final class ConnectorIamActions {
    public static final String SERVICE = "connector";

    public static final IamAction READ = IamActions.of(SERVICE, "ReadConnector");
    public static final IamAction CONNECT_SESSION = IamActions.of(SERVICE, "ConnectSession");
    public static final IamAction DISCONNECT_SESSION = IamActions.of(SERVICE, "DisconnectSession");
    public static final IamAction READ_BUSINESS_PARTNERS = IamActions.of(SERVICE, "ReadBusinessPartners");
    public static final IamAction CONFIGURE = IamActions.of(SERVICE, "ConfigureConnector");
    public static final IamAction START_SYNC = IamActions.of(SERVICE, "StartSync");

    private static final List<IamActionDescriptor> CATALOG = List.of(
            IamActionDescriptor.read(READ, "Read connector configuration, availability, and session status"),
            IamActionDescriptor.write(CONNECT_SESSION, "Open an authenticated connector session"),
            IamActionDescriptor.write(DISCONNECT_SESSION, "Close an authenticated connector session"),
            IamActionDescriptor.read(READ_BUSINESS_PARTNERS, "Read business partner data through a connector session"),
            IamActionDescriptor.admin(CONFIGURE, "Configure connector settings"),
            IamActionDescriptor.system(START_SYNC, "Start connector synchronization")
    );

    private ConnectorIamActions() {}

    public static List<IamActionDescriptor> catalog() {
        return CATALOG;
    }
}
