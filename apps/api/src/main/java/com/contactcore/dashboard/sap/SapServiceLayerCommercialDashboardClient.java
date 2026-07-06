// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.sap;

import com.contactcore.connector.port.ConnectorExecutionContext;
import com.contactcore.dashboard.application.CommercialDashboardDocument;
import com.contactcore.dashboard.application.CommercialDashboardDocumentType;
import com.contactcore.dashboard.application.CommercialDashboardQuery;
import com.contactcore.dashboard.application.CommercialDashboardSnapshot;
import com.contactcore.dashboard.application.SapCommercialDashboardClient;
import com.contactcore.sapb1.resource.dashboard.SapB1CommercialDashboardGateway;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SapServiceLayerCommercialDashboardClient implements SapCommercialDashboardClient {
    private final SapB1CommercialDashboardGateway gateway;
    private final SapDashboardMapper mapper;

    public SapServiceLayerCommercialDashboardClient(SapB1CommercialDashboardGateway gateway, SapDashboardMapper mapper) {
        this.gateway = gateway;
        this.mapper = mapper;
    }

    @Override
    public CommercialDashboardSnapshot fetch(ConnectorExecutionContext context, CommercialDashboardQuery query) {
        List<CommercialDashboardDocument> documents = new ArrayList<>();
        for (CommercialDashboardDocumentType type : CommercialDashboardDocumentType.values()) {
            gateway.readDocuments(context, type, query).stream()
                    .map(dto -> mapper.toDocument(type, dto))
                    .forEach(documents::add);
        }
        return new CommercialDashboardSnapshot(documents);
    }
}
