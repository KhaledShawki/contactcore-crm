// Copyright (c) Khaled Shawki. All rights reserved.

import type { FetchBaseQueryError } from '@reduxjs/toolkit/query';
import type { SerializedError } from '@reduxjs/toolkit';
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
import { commercialDashboardQueryArgs, type SchemaFilterValues } from './schemaFilters';

export type SchemaDataSourceStatus = 'idle' | 'loading' | 'ready' | 'empty' | 'forbidden' | 'notConnected' | 'error';

export interface SchemaDataSourceEntry {
  value?: unknown;
  status: SchemaDataSourceStatus;
}

export interface SchemaDataSourceState {
  values: Record<string, unknown>;
  entries: Record<string, SchemaDataSourceEntry>;
  isLoading: boolean;
  hasError: boolean;
}

type QueryLike<T> = {
  data?: T;
  isLoading: boolean;
  isFetching: boolean;
  error?: FetchBaseQueryError | SerializedError;
};

export function useSchemaDataSources(screen: UiScreen | undefined, filterValues: SchemaFilterValues = {}): SchemaDataSourceState {
  const requiredDataSources = collectRequiredDataSources(screen);
  const requires = (key: string) => requiredDataSources.has(key);
  const commercialArgs = commercialDashboardQueryArgs(filterValues);

  const dashboard = useGetDashboardQuery(undefined, { skip: !requires('analytics.dashboard') });
  const commercialSummary = useGetCommercialDashboardSummaryQuery(commercialArgs, { skip: !requires('commercialDashboard.summary') });
  const topSellingItems = useGetCommercialDashboardTopSellingItemsQuery(commercialArgs, { skip: !requires('commercialDashboard.topSellingItems') });
  const topCustomers = useGetCommercialDashboardTopCustomersQuery(commercialArgs, { skip: !requires('commercialDashboard.topCustomers') });
  const unpaidInvoices = useGetCommercialDashboardUnpaidInvoicesQuery(commercialArgs, { skip: !requires('commercialDashboard.unpaidInvoices') });
  const invoiceAging = useGetCommercialDashboardInvoiceAgingQuery(commercialArgs, { skip: !requires('commercialDashboard.invoiceAging') });
  const salesTrend = useGetCommercialDashboardSalesTrendQuery(commercialArgs, { skip: !requires('commercialDashboard.salesTrend') });
  const salesByDocumentType = useGetCommercialDashboardSalesByDocumentTypeQuery(commercialArgs, { skip: !requires('commercialDashboard.salesByDocumentType') });

  const entries: Record<string, SchemaDataSourceEntry> = {
    'analytics.dashboard': entry(dashboard),
    'commercialDashboard.summary': entry(commercialSummary, (summary) => ({ kpis: commercialSummaryKpis(summary) })),
    'commercialDashboard.topSellingItems': entry(topSellingItems),
    'commercialDashboard.topCustomers': entry(topCustomers),
    'commercialDashboard.unpaidInvoices': entry(unpaidInvoices),
    'commercialDashboard.invoiceAging': entry(invoiceAging),
    'commercialDashboard.salesTrend': entry(salesTrend),
    'commercialDashboard.salesByDocumentType': entry(salesByDocumentType),
  };

  const activeEntries = [...requiredDataSources].flatMap((key) => {
    const dataSourceEntry = entries[key];
    return dataSourceEntry ? [dataSourceEntry] : [];
  });

  return {
    values: readyValues(entries),
    entries,
    isLoading: activeEntries.some((value) => value.status === 'loading'),
    hasError: activeEntries.some((value) => ['forbidden', 'notConnected', 'error'].includes(value.status)),
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

function readyValues(entries: Record<string, SchemaDataSourceEntry>): Record<string, unknown> {
  const values: Record<string, unknown> = {};

  for (const [key, value] of Object.entries(entries)) {
    if (value.status === 'ready') {
      values[key] = value.value;
    }
  }

  return values;
}

function entry<T>(query: QueryLike<T>, map: (value: T) => unknown = (value) => value): SchemaDataSourceEntry {
  if (query.isLoading || query.isFetching) {
    return { status: 'loading' };
  }
  if (query.error) {
    return { status: classifyError(query.error) };
  }
  if (query.data === undefined) {
    return { status: 'idle' };
  }
  const value = map(query.data);
  if (isEmptyValue(value)) {
    return { value, status: 'empty' };
  }
  return { value, status: 'ready' };
}

function classifyError(error: FetchBaseQueryError | SerializedError): SchemaDataSourceStatus {
  if ('status' in error) {
    if (error.status === 401 || error.status === 403) return 'forbidden';
    if ('data' in error && apiErrorCode(error.data) === 'CONNECTOR_SESSION_REQUIRED') return 'notConnected';
  }
  return 'error';
}

function apiErrorCode(data: unknown): string | undefined {
  if (data && typeof data === 'object' && 'code' in data) {
    const code = (data as { code?: unknown }).code;
    return typeof code === 'string' ? code : undefined;
  }
  return undefined;
}

function isEmptyValue(value: unknown): boolean {
  if (Array.isArray(value)) return value.length === 0;
  if (value && typeof value === 'object' && 'kpis' in value) {
    const kpis = (value as { kpis?: unknown }).kpis;
    return Array.isArray(kpis) && kpis.length === 0;
  }
  return false;
}

function kpi(key: string, label: string, value: number, unit: string, helpText: string): KpiMetric {
  return { key, label, value, unit, helpText };
}
