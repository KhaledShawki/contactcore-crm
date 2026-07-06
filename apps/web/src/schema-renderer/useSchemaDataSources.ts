// Copyright (c) Khaled Shawki. All rights reserved.

import { useGetDashboardQuery } from '../analytics/analyticsApi';
import {
  useGetCommercialDashboardInvoiceAgingQuery,
  useGetCommercialDashboardSalesByDocumentTypeQuery,
  useGetCommercialDashboardSalesTrendQuery,
  useGetCommercialDashboardSummaryQuery,
  useGetCommercialDashboardTopCustomersQuery,
  useGetCommercialDashboardTopSellingItemsQuery,
  useGetCommercialDashboardUnpaidInvoicesQuery,
  type CommercialDashboardSummaryResponse,
} from '../dashboard/commercialDashboardApi';
import type { KpiMetric, UiScreen } from '../schema/types';

export interface SchemaDataSourceState {
  values: Record<string, unknown>;
  isLoading: boolean;
  hasError: boolean;
}

export function useSchemaDataSources(screen: UiScreen | undefined): SchemaDataSourceState {
  const requiredDataSources = collectRequiredDataSources(screen);
  const requires = (key: string) => requiredDataSources.has(key);

  const dashboard = useGetDashboardQuery(undefined, { skip: !requires('analytics.dashboard') });
  const commercialSummary = useGetCommercialDashboardSummaryQuery(undefined, { skip: !requires('commercialDashboard.summary') });
  const topSellingItems = useGetCommercialDashboardTopSellingItemsQuery(undefined, { skip: !requires('commercialDashboard.topSellingItems') });
  const topCustomers = useGetCommercialDashboardTopCustomersQuery(undefined, { skip: !requires('commercialDashboard.topCustomers') });
  const unpaidInvoices = useGetCommercialDashboardUnpaidInvoicesQuery(undefined, { skip: !requires('commercialDashboard.unpaidInvoices') });
  const invoiceAging = useGetCommercialDashboardInvoiceAgingQuery(undefined, { skip: !requires('commercialDashboard.invoiceAging') });
  const salesTrend = useGetCommercialDashboardSalesTrendQuery(undefined, { skip: !requires('commercialDashboard.salesTrend') });
  const salesByDocumentType = useGetCommercialDashboardSalesByDocumentTypeQuery(undefined, { skip: !requires('commercialDashboard.salesByDocumentType') });

  return {
    values: {
      ...(dashboard.data ? { 'analytics.dashboard': dashboard.data } : {}),
      ...(commercialSummary.data ? { 'commercialDashboard.summary': { kpis: commercialSummaryKpis(commercialSummary.data) } } : {}),
      ...(topSellingItems.data ? { 'commercialDashboard.topSellingItems': topSellingItems.data } : {}),
      ...(topCustomers.data ? { 'commercialDashboard.topCustomers': topCustomers.data } : {}),
      ...(unpaidInvoices.data ? { 'commercialDashboard.unpaidInvoices': unpaidInvoices.data } : {}),
      ...(invoiceAging.data ? { 'commercialDashboard.invoiceAging': invoiceAging.data } : {}),
      ...(salesTrend.data ? { 'commercialDashboard.salesTrend': salesTrend.data } : {}),
      ...(salesByDocumentType.data ? { 'commercialDashboard.salesByDocumentType': salesByDocumentType.data } : {}),
    },
    isLoading: [
      dashboard,
      commercialSummary,
      topSellingItems,
      topCustomers,
      unpaidInvoices,
      invoiceAging,
      salesTrend,
      salesByDocumentType,
    ].some((query) => query.isLoading || query.isFetching),
    hasError: [
      dashboard,
      commercialSummary,
      topSellingItems,
      topCustomers,
      unpaidInvoices,
      invoiceAging,
      salesTrend,
      salesByDocumentType,
    ].some((query) => Boolean(query.error)),
  };
}

export function collectRequiredDataSources(screen: UiScreen | undefined): Set<string> {
  const keys = new Set<string>();
  screen?.layout?.sections.forEach((section) => {
    if (section.visible === false) return;
    section.widgets.forEach((widget) => {
      if (widget.visible === false) return;
      const key = widget.dataSource?.key;
      if (key) keys.add(key);
    });
  });
  return keys;
}

export function commercialSummaryKpis(summary: CommercialDashboardSummaryResponse): KpiMetric[] {
  const currency = summary.currency || '';
  return [
    kpi('commercialTotalSales', 'Total sales', summary.totalSales, currency, 'Total invoice amount for the selected period.'),
    kpi('commercialOpenInvoices', 'Open invoices', summary.openInvoiceAmount, currency, 'Unpaid invoice amount still open.'),
    kpi('commercialOverdueAmount', 'Overdue amount', summary.overdueAmount, currency, 'Open invoice amount past due date.'),
    kpi('commercialUnpaidInvoiceCount', 'Unpaid invoices', summary.unpaidInvoiceCount, '', 'Number of unpaid invoices.'),
    kpi('commercialActiveCustomers', 'Active customers', summary.activeCustomerCount, '', 'Customers with sales in the selected period.'),
    kpi('commercialSoldItems', 'Sold items', summary.soldItemCount, '', 'Distinct sold items in the selected period.'),
  ];
}

function kpi(key: string, label: string, value: number, unit: string, helpText: string): KpiMetric {
  return { key, label, value, unit, helpText };
}
