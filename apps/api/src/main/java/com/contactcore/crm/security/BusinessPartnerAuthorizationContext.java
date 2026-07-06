// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.security;

import com.contactcore.crm.api.BusinessPartnerWriteRequest;
import com.contactcore.iam.evaluation.IamRequestContext;
import java.util.LinkedHashMap;
import java.util.Map;

public record BusinessPartnerAuthorizationContext(
        String kindCode,
        String statusCode,
        String leadSourceCode,
        String query,
        String operation
) {
    public static BusinessPartnerAuthorizationContext empty() {
        return new BusinessPartnerAuthorizationContext(null, null, null, null, null);
    }

    public static BusinessPartnerAuthorizationContext forSearch(String kindCode, String query) {
        return new BusinessPartnerAuthorizationContext(kindCode, null, null, query, "search");
    }

    public static BusinessPartnerAuthorizationContext forWrite(BusinessPartnerWriteRequest request, String operation) {
        if (request == null) {
            return new BusinessPartnerAuthorizationContext(null, null, null, null, operation);
        }
        return new BusinessPartnerAuthorizationContext(
                request.kind(),
                request.statusCode(),
                request.sourceCode(),
                null,
                operation
        );
    }

    public static BusinessPartnerAuthorizationContext forOperation(String operation) {
        return new BusinessPartnerAuthorizationContext(null, null, null, null, operation);
    }

    public IamRequestContext toIamContext() {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "businessPartnerKind", kindCode);
        put(values, "businessPartnerStatus", statusCode);
        put(values, "leadSourceCode", leadSourceCode);
        put(values, "query", query);
        put(values, "operation", operation);
        return new IamRequestContext(values);
    }

    private static void put(Map<String, Object> values, String key, String value) {
        if (value != null && !value.isBlank()) {
            values.put(key, value.trim());
        }
    }
}
