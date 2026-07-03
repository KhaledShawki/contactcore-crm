// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.connector;

import com.contactcore.assistant.retrieval.AssistantRecordReference;
import com.contactcore.assistant.retrieval.AssistantSearchResult;
import com.contactcore.connector.model.CrmBusinessPartnerView;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ConnectorAssistantResultMapper {
    private ConnectorAssistantResultMapper() {}

    public static List<AssistantSearchResult> businessPartners(List<CrmBusinessPartnerView> views) {
        return views.stream().map(ConnectorAssistantResultMapper::businessPartner).toList();
    }

    public static AssistantSearchResult businessPartner(CrmBusinessPartnerView view) {
        Map<String, String> fields = new LinkedHashMap<>();
        put(fields, "Kind", view.kind());
        put(fields, "Status", view.statusName());
        put(fields, "Primary email", view.primaryEmail());
        put(fields, "Primary phone", view.primaryPhone());
        put(fields, "Website", view.website());
        put(fields, "External code", view.code());
        put(fields, "Source system", view.sourceSystem());
        put(fields, "Connector", view.connectorDisplayName());
        put(fields, "Currency", view.currency());
        put(fields, "Balance", money(view.balance()));
        String route = "/connectors/business-partners/" + URLEncoder.encode(view.externalId(), StandardCharsets.UTF_8);
        return AssistantSearchResult.of(
                new AssistantRecordReference("CONNECTOR_BUSINESS_PARTNER", null, view.name() + " (" + view.code() + ")", route),
                fields
        );
    }

    private static void put(Map<String, String> fields, String key, String value) {
        if (value != null && !value.isBlank()) {
            fields.put(key, value);
        }
    }

    private static String money(BigDecimal value) {
        return value == null ? null : value.stripTrailingZeros().toPlainString();
    }
}
