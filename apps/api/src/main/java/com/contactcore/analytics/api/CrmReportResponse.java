// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.analytics.api;

import java.util.List;

public record CrmReportResponse(
        List<KpiResponse> kpis,
        List<ChartPointResponse> kindBreakdown,
        List<ChartPointResponse> statusBreakdown,
        List<ChartPointResponse> marketingSourceBreakdown,
        List<ChartPointResponse> contactPersonsByRole,
        List<ChartPointResponse> contactCoverageByKind,
        List<MarketingSourceReportRowResponse> marketingSourcePerformance,
        List<RecentBusinessPartnerResponse> recentBusinessPartners
) {}
