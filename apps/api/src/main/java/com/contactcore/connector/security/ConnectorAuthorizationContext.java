// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.security;

import com.contactcore.connector.model.CrmBusinessPartnerSearchCriteria;
import com.contactcore.iam.evaluation.IamRequestContext;
import java.util.LinkedHashMap;
import java.util.Map;

public record ConnectorAuthorizationContext(
        Long connectorInstanceId,
        String externalId,
        String businessPartnerType,
        String query,
        String operation
) {
    public static ConnectorAuthorizationContext empty() {
        return new ConnectorAuthorizationContext(null, null, null, null, null);
    }

    public static ConnectorAuthorizationContext forSession(Long connectorInstanceId, String operation) {
        return new ConnectorAuthorizationContext(connectorInstanceId, null, null, null, operation);
    }

    public static ConnectorAuthorizationContext forBusinessPartnerSearch(CrmBusinessPartnerSearchCriteria criteria) {
        if (criteria == null) {
            return new ConnectorAuthorizationContext(null, null, null, null, "searchBusinessPartners");
        }
        return new ConnectorAuthorizationContext(
                null,
                null,
                criteria.type() == null ? null : criteria.type().name(),
                criteria.query(),
                "searchBusinessPartners"
        );
    }

    public static ConnectorAuthorizationContext forBusinessPartnerRead(String externalId) {
        return new ConnectorAuthorizationContext(null, externalId, null, null, "readBusinessPartner");
    }

    public IamRequestContext toRequestContext() {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "connectorInstanceId", connectorInstanceId);
        put(values, "externalId", externalId);
        put(values, "businessPartnerType", businessPartnerType);
        put(values, "query", query);
        put(values, "operation", operation);
        return new IamRequestContext(values);
    }

    private static void put(Map<String, Object> values, String key, Object value) {
        if (value != null) {
            values.put(key, value);
        }
    }
}
