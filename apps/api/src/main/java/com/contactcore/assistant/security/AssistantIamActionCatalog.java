// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.iam.domain.IamActionCatalog;
import com.contactcore.iam.domain.IamActionDescriptor;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AssistantIamActionCatalog implements IamActionCatalog {
    @Override
    public String service() {
        return AssistantIamActions.SERVICE;
    }

    @Override
    public List<IamActionDescriptor> actions() {
        return AssistantIamActions.catalog();
    }
}
