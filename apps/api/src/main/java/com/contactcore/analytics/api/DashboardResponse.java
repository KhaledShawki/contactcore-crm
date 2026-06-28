// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.analytics.api;

import java.util.List;

public record DashboardResponse(
        List<KpiResponse> kpis,
        List<ChartPointResponse> businessPartnersByKind,
        List<ChartPointResponse> leadsByMarketingSource,
        List<ChartPointResponse> businessPartnersByStatus,
        List<MonthlyCountResponse> newBusinessPartnersByMonth,
        List<ChartPointResponse> contactPersonsByRole,
        List<ChartPointResponse> contactCoverageByKind,
        List<RecentBusinessPartnerResponse> recentBusinessPartners
) {}
