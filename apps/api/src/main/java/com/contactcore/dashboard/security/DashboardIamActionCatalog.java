// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.security;

import com.contactcore.iam.domain.IamActionCatalog;
import com.contactcore.iam.domain.IamActionDescriptor;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DashboardIamActionCatalog implements IamActionCatalog {
    @Override
    public String service() {
        return DashboardIamActions.SERVICE;
    }

    @Override
    public List<IamActionDescriptor> actions() {
        return List.of(
                IamActionDescriptor.read(DashboardIamActions.READ_COMMERCIAL_DASHBOARD, "Read commercial dashboard widgets"),
                IamActionDescriptor.read(DashboardIamActions.READ_COMMERCIAL_FINANCIALS, "Read commercial financial dashboard widgets")
        );
    }
}
