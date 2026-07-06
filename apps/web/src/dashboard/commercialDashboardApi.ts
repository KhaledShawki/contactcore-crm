// Copyright (c) Khaled Shawki. All rights reserved.

import { baseApi } from '../api/baseApi';

export interface CommercialDashboardQueryArgs {
  from?: string;
  to?: string;
  currency?: string;
  limit?: number;
  groupBy?: 'MONTH' | string;
}

export interface CommercialDashboardSummaryResponse {
  totalSales: number;
  openInvoiceAmount: number;
  overdueAmount: number;
  unpaidInvoiceCount: number;
  activeCustomerCount: number;
  soldItemCount: number;
  currency: string;
}

export interface TopSellingItemResponse {
  itemCode: string;
  itemName: string;
  quantitySold: number;
  netAmount: number;
  currency: string;
}

export interface TopCustomerResponse {
  businessPartnerCode: string;
  businessPartnerName: string;
  netAmount: number;
  currency: string;
}

export interface UnpaidInvoiceCustomerResponse {
  businessPartnerCode: string;
  businessPartnerName: string;
  openAmount: number;
  currency: string;
  invoiceCount: number;
  oldestDueDate: string | null;
  maxOverdueDays: number;
}

export interface InvoiceAgingBucketResponse {
  bucket: string;
  openAmount: number;
  currency: string;
}

export interface SalesTrendPointResponse {
  period: string;
  netAmount: number;
  currency: string;
}

export interface SalesByDocumentTypeResponse {
  documentType: string;
  netAmount: number;
  currency: string;
}

const commercialDashboardApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getCommercialDashboardSummary: builder.query<CommercialDashboardSummaryResponse, CommercialDashboardQueryArgs | undefined>({
      query: (args) => commercialDashboardUrl('/dashboard/commercial/summary', args),
      providesTags: [{ type: 'Dashboard', id: 'COMMERCIAL_SUMMARY' }],
    }),
    getCommercialDashboardTopSellingItems: builder.query<TopSellingItemResponse[], CommercialDashboardQueryArgs | undefined>({
      query: (args) => commercialDashboardUrl('/dashboard/commercial/top-selling-items', args),
      providesTags: [{ type: 'Dashboard', id: 'COMMERCIAL_TOP_SELLING_ITEMS' }],
    }),
    getCommercialDashboardTopCustomers: builder.query<TopCustomerResponse[], CommercialDashboardQueryArgs | undefined>({
      query: (args) => commercialDashboardUrl('/dashboard/commercial/top-customers', args),
      providesTags: [{ type: 'Dashboard', id: 'COMMERCIAL_TOP_CUSTOMERS' }],
    }),
    getCommercialDashboardUnpaidInvoices: builder.query<UnpaidInvoiceCustomerResponse[], CommercialDashboardQueryArgs | undefined>({
      query: (args) => commercialDashboardUrl('/dashboard/commercial/unpaid-invoices', args),
      providesTags: [{ type: 'Dashboard', id: 'COMMERCIAL_UNPAID_INVOICES' }],
    }),
    getCommercialDashboardInvoiceAging: builder.query<InvoiceAgingBucketResponse[], CommercialDashboardQueryArgs | undefined>({
      query: (args) => commercialDashboardUrl('/dashboard/commercial/invoice-aging', args),
      providesTags: [{ type: 'Dashboard', id: 'COMMERCIAL_INVOICE_AGING' }],
    }),
    getCommercialDashboardSalesTrend: builder.query<SalesTrendPointResponse[], CommercialDashboardQueryArgs | undefined>({
      query: (args) => commercialDashboardUrl('/dashboard/commercial/sales-trend', args),
      providesTags: [{ type: 'Dashboard', id: 'COMMERCIAL_SALES_TREND' }],
    }),
    getCommercialDashboardSalesByDocumentType: builder.query<SalesByDocumentTypeResponse[], CommercialDashboardQueryArgs | undefined>({
      query: (args) => commercialDashboardUrl('/dashboard/commercial/sales-by-document-type', args),
      providesTags: [{ type: 'Dashboard', id: 'COMMERCIAL_SALES_BY_DOCUMENT_TYPE' }],
    }),
  }),
});

function commercialDashboardUrl(path: string, args: CommercialDashboardQueryArgs | undefined): string {
  const params = new URLSearchParams();
  if (args?.from) params.set('from', args.from);
  if (args?.to) params.set('to', args.to);
  if (args?.currency) params.set('currency', args.currency);
  if (args?.limit != null) params.set('limit', String(args.limit));
  if (args?.groupBy) params.set('groupBy', args.groupBy);
  const query = params.toString();
  return query ? `${path}?${query}` : path;
}

export const {
  useGetCommercialDashboardSummaryQuery,
  useGetCommercialDashboardTopSellingItemsQuery,
  useGetCommercialDashboardTopCustomersQuery,
  useGetCommercialDashboardUnpaidInvoicesQuery,
  useGetCommercialDashboardInvoiceAgingQuery,
  useGetCommercialDashboardSalesTrendQuery,
  useGetCommercialDashboardSalesByDocumentTypeQuery,
} = commercialDashboardApi;
