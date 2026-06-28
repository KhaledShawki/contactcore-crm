// Copyright (c) Khaled Shawki. All rights reserved.

import { baseApi } from '../api/baseApi';
import { filenameFromContentDisposition, type DownloadedReport } from './reportDownload';

interface DownloadBusinessPartnerReportArgs {
  kind: string;
  query: string;
  sort?: string;
  maxRows?: number;
}

interface DownloadMarketingSourcesReportArgs {
  query: string;
  maxRows?: number;
}

const DEFAULT_MAX_ROWS = 5000;

const reportsApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    downloadBusinessPartnerReport: builder.mutation<DownloadedReport, DownloadBusinessPartnerReportArgs>({
      query: ({ kind, query, sort = 'updated_desc', maxRows = DEFAULT_MAX_ROWS }) => ({
        url: `/reports/business-partners.xlsx?kind=${encodeURIComponent(kind)}&q=${encodeURIComponent(query)}&sort=${encodeURIComponent(sort)}&maxRows=${maxRows}`,
        method: 'GET',
        responseHandler: (response) => response.blob(),
      }),
      transformResponse: (blob: Blob, meta) => ({
        blob,
        filename: filenameFromContentDisposition(meta?.response?.headers.get('content-disposition'), 'contactcore-business-partners.xlsx'),
      }),
    }),
    downloadCrmSummaryReport: builder.mutation<DownloadedReport, void>({
      query: () => ({
        url: '/reports/crm-summary.xlsx',
        method: 'GET',
        responseHandler: (response) => response.blob(),
      }),
      transformResponse: (blob: Blob, meta) => ({
        blob,
        filename: filenameFromContentDisposition(meta?.response?.headers.get('content-disposition'), 'contactcore-crm-report.xlsx'),
      }),
    }),
    downloadMarketingSourcesReport: builder.mutation<DownloadedReport, DownloadMarketingSourcesReportArgs>({
      query: ({ query, maxRows = DEFAULT_MAX_ROWS }) => ({
        url: `/reports/marketing-sources.xlsx?q=${encodeURIComponent(query)}&maxRows=${maxRows}`,
        method: 'GET',
        responseHandler: (response) => response.blob(),
      }),
      transformResponse: (blob: Blob, meta) => ({
        blob,
        filename: filenameFromContentDisposition(meta?.response?.headers.get('content-disposition'), 'contactcore-marketing-sources.xlsx'),
      }),
    }),
  }),
});

export const {
  useDownloadBusinessPartnerReportMutation,
  useDownloadCrmSummaryReportMutation,
  useDownloadMarketingSourcesReportMutation,
} = reportsApi;
