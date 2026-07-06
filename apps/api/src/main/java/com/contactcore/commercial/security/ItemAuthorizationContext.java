// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.security;

import com.contactcore.commercial.domain.CommercialSourceSystem;
import com.contactcore.iam.evaluation.IamRequestContext;
import java.util.LinkedHashMap;
import java.util.Map;

public record ItemAuthorizationContext(
        CommercialSourceSystem sourceSystem,
        Boolean active
) {
    public IamRequestContext toIamContext() {
        Map<String, Object> values = new LinkedHashMap<>();
        if (sourceSystem != null) {
            values.put("sourceSystem", sourceSystem.name());
        }
        if (active != null) {
            values.put("active", active);
        }
        return new IamRequestContext(values);
    }
}
