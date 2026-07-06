// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.application;

import com.contactcore.connector.application.ConnectorSessionService;
import com.contactcore.connector.application.ConnectorSessionState;
import com.contactcore.connector.model.CrmConnectorCapability;
import com.contactcore.connector.port.ConnectorExecutionContext;
import com.contactcore.dashboard.api.CommercialDashboardSummaryResponse;
import com.contactcore.dashboard.api.InvoiceAgingBucketResponse;
import com.contactcore.dashboard.api.SalesByDocumentTypeResponse;
import com.contactcore.dashboard.api.SalesTrendPointResponse;
import com.contactcore.dashboard.api.TopCustomerResponse;
import com.contactcore.dashboard.api.TopSellingItemResponse;
import com.contactcore.dashboard.api.UnpaidInvoiceCustomerResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommercialDashboardService {
    private final ConnectorSessionService sessions;
    private final SapCommercialDashboardClient sapClient;
    private final CommercialDashboardAggregator aggregator;
    private final Clock clock;

    @Autowired
    public CommercialDashboardService(ConnectorSessionService sessions,
                                      SapCommercialDashboardClient sapClient,
                                      CommercialDashboardAggregator aggregator) {
        this(sessions, sapClient, aggregator, Clock.systemDefaultZone());
    }

    CommercialDashboardService(ConnectorSessionService sessions,
                               SapCommercialDashboardClient sapClient,
                               CommercialDashboardAggregator aggregator,
                               Clock clock) {
        this.sessions = sessions;
        this.sapClient = sapClient;
        this.aggregator = aggregator;
        this.clock = clock;
    }

    public CommercialDashboardQuery query(LocalDate from, LocalDate to, String currency, Integer limit, String groupBy) {
        return CommercialDashboardQuery.of(from, to, currency, limit, groupBy, clock);
    }

    public CommercialDashboardSummaryResponse summary(Long userId, CommercialDashboardQuery query) {
        return aggregator.summary(snapshot(userId, query), query);
    }

    public List<TopSellingItemResponse> topSellingItems(Long userId, CommercialDashboardQuery query) {
        return aggregator.topSellingItems(snapshot(userId, query), query);
    }

    public List<TopCustomerResponse> topCustomers(Long userId, CommercialDashboardQuery query) {
        return aggregator.topCustomers(snapshot(userId, query), query);
    }

    public List<UnpaidInvoiceCustomerResponse> unpaidInvoices(Long userId, CommercialDashboardQuery query) {
        return aggregator.unpaidInvoiceCustomers(snapshot(userId, query), query);
    }

    public List<InvoiceAgingBucketResponse> invoiceAging(Long userId, CommercialDashboardQuery query) {
        return aggregator.invoiceAging(snapshot(userId, query), query);
    }

    public List<SalesTrendPointResponse> salesTrend(Long userId, CommercialDashboardQuery query) {
        return aggregator.salesTrend(snapshot(userId, query), query);
    }

    public List<SalesByDocumentTypeResponse> salesByDocumentType(Long userId, CommercialDashboardQuery query) {
        return aggregator.salesByDocumentType(snapshot(userId, query), query);
    }

    private CommercialDashboardSnapshot snapshot(Long userId, CommercialDashboardQuery query) {
        ConnectorSessionState session = sessions.requireActiveSession(userId, CrmConnectorCapability.READ_DOCUMENTS);
        ConnectorExecutionContext context = new ConnectorExecutionContext(userId, session.instance(), session.adapterSession());
        return sapClient.fetch(context, query);
    }

}
