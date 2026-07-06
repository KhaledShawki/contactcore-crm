// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.application;

import com.contactcore.connector.port.ConnectorExecutionContext;

public interface SapCommercialDashboardClient {
    CommercialDashboardSnapshot fetch(ConnectorExecutionContext context, CommercialDashboardQuery query);
}
