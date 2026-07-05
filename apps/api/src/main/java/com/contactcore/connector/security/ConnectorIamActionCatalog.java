// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.security;

import com.contactcore.iam.domain.IamActionCatalog;
import com.contactcore.iam.domain.IamActionDescriptor;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ConnectorIamActionCatalog implements IamActionCatalog {
    @Override
    public String service() {
        return ConnectorIamActions.SERVICE;
    }

    @Override
    public List<IamActionDescriptor> actions() {
        return ConnectorIamActions.catalog();
    }
}
