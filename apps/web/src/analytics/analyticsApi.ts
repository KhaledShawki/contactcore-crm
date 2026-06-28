// Copyright (c) Khaled Shawki. All rights reserved.

import { baseApi } from '../api/baseApi';
import type { CrmReportResponse, DashboardResponse } from '../schema/types';

const analyticsApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getDashboard: builder.query<DashboardResponse, void>({
      query: () => '/dashboard',
      providesTags: [{ type: 'Dashboard', id: 'MAIN' }],
    }),
    getCrmReport: builder.query<CrmReportResponse, void>({
      query: () => '/reports/crm',
      providesTags: [{ type: 'Report', id: 'CRM' }],
    }),
  }),
});

export const { useGetDashboardQuery, useGetCrmReportQuery } = analyticsApi;
