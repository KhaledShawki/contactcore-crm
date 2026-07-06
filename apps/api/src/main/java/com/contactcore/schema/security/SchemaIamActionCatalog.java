// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.security;

import com.contactcore.iam.domain.IamActionCatalog;
import com.contactcore.iam.domain.IamActionDescriptor;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SchemaIamActionCatalog implements IamActionCatalog {
    @Override
    public String service() {
        return SchemaIamActions.SERVICE;
    }

    @Override
    public List<IamActionDescriptor> actions() {
        return SchemaIamActions.catalog();
    }
}
