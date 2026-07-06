// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.dashboard;

import com.contactcore.connector.port.ConnectorExecutionContext;
import com.contactcore.dashboard.application.CommercialDashboardDocumentType;
import com.contactcore.dashboard.application.CommercialDashboardQuery;
import com.contactcore.sapb1.client.SapB1ServiceLayerClient;
import com.contactcore.sapb1.model.SapB1ConnectorConfiguration;
import com.contactcore.sapb1.model.SapB1Session;
import com.contactcore.sapb1.odata.SapB1ODataFilter;
import com.contactcore.sapb1.odata.SapB1ODataPathBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SapB1CommercialDashboardGateway {
    private static final int PAGE_SIZE = 100;
    private static final int MAX_DOCUMENTS_PER_TYPE = 2_000;

    private static final String[] DOCUMENT_FIELDS = {
            "DocEntry",
            "DocNum",
            "CardCode",
            "CardName",
            "DocDate",
            "DocDueDate",
            "DocTotal",
            "PaidToDate",
            "DocCurrency",
            "DocumentStatus",
            "Cancelled",
            "DocumentLines"
    };

    private final ObjectMapper objectMapper;
    private final SapB1ServiceLayerClient client;
    private final SapB1ODataPathBuilder pathBuilder;

    public SapB1CommercialDashboardGateway(ObjectMapper objectMapper,
                                           SapB1ServiceLayerClient client,
                                           SapB1ODataPathBuilder pathBuilder) {
        this.objectMapper = objectMapper;
        this.client = client;
        this.pathBuilder = pathBuilder;
    }

    public List<SapB1DashboardDocumentDto> readDocuments(ConnectorExecutionContext context,
                                                          CommercialDashboardDocumentType type,
                                                          CommercialDashboardQuery query) {
        List<SapB1DashboardDocumentDto> result = new ArrayList<>();
        int skip = 0;
        while (result.size() < MAX_DOCUMENTS_PER_TYPE) {
            String path = pathBuilder.entitySet(entitySet(type))
                    .select(DOCUMENT_FIELDS)
                    .filter(filter(query))
                    .orderBy("DocDate asc, DocEntry asc")
                    .skip(skip)
                    .top(PAGE_SIZE)
                    .build();
            List<SapB1DashboardDocumentDto> page = client.get(configuration(context), session(context), path, SapB1DashboardDocumentCollectionResponse.class).value();
            if (page.isEmpty()) {
                break;
            }
            result.addAll(page);
            if (page.size() < PAGE_SIZE) {
                break;
            }
            skip += PAGE_SIZE;
        }
        return result.size() > MAX_DOCUMENTS_PER_TYPE ? result.subList(0, MAX_DOCUMENTS_PER_TYPE) : List.copyOf(result);
    }

    private SapB1ODataFilter filter(CommercialDashboardQuery query) {
        List<SapB1ODataFilter> filters = new ArrayList<>();
        filters.add(SapB1ODataFilter.raw("DocDate ge '" + query.dateRange().from() + "'"));
        filters.add(SapB1ODataFilter.raw("DocDate le '" + query.dateRange().to() + "'"));
        filters.add(SapB1ODataFilter.raw("Cancelled eq 'tNO'"));
        if (query.currency() != null) {
            filters.add(SapB1ODataFilter.eq("DocCurrency", query.currency()));
        }
        return SapB1ODataFilter.all(filters);
    }

    private String entitySet(CommercialDashboardDocumentType type) {
        return switch (type) {
            case QUOTATION -> "Quotations";
            case SALES_ORDER -> "Orders";
            case DELIVERY_NOTE -> "DeliveryNotes";
            case INVOICE -> "Invoices";
            case CREDIT_NOTE -> "CreditNotes";
        };
    }

    private SapB1ConnectorConfiguration configuration(ConnectorExecutionContext context) {
        return SapB1ConnectorConfiguration.from(context.instance(), objectMapper);
    }

    private SapB1Session session(ConnectorExecutionContext context) {
        if (context.session() instanceof SapB1Session sapSession) {
            return sapSession;
        }
        throw new IllegalStateException("Active connector session is not a SAP B1 session.");
    }
}
