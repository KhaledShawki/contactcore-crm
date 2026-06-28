// Copyright (c) Khaled Shawki. All rights reserved.

import { baseApi } from '../api/baseApi';
import type { MarketingSource, PageResponse } from '../schema/types';

interface SearchMarketingSourcesArgs {
  query: string;
  page: number;
  size: number;
}

interface SaveMarketingSourceArgs {
  id?: number;
  body: Partial<MarketingSource>;
}

const marketingApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    searchMarketingSources: builder.query<PageResponse<MarketingSource>, SearchMarketingSourcesArgs>({
      query: ({ query, page, size }) => `/marketing/sources?q=${encodeURIComponent(query)}&page=${page}&size=${size}`,
      providesTags: (result) => [
        { type: 'MarketingSource', id: 'LIST' },
        ...(result?.items.map((source) => ({ type: 'MarketingSource' as const, id: source.id })) ?? []),
      ],
    }),
    saveMarketingSource: builder.mutation<MarketingSource, SaveMarketingSourceArgs>({
      query: ({ id, body }) => ({
        url: id ? `/marketing/sources/${id}` : '/marketing/sources',
        method: id ? 'PUT' : 'POST',
        body: normalizeMarketingSourcePayload(body),
      }),
      invalidatesTags: (_result, _error, args) => [
        { type: 'MarketingSource', id: 'LIST' },
        'UiSchema',
        { type: 'Dashboard' as const, id: 'MAIN' },
        { type: 'Report' as const, id: 'CRM' },
        ...(args.id ? [{ type: 'MarketingSource' as const, id: args.id }] : []),
      ],
    }),
    archiveMarketingSource: builder.mutation<void, number>({
      query: (id) => ({ url: `/marketing/sources/${id}`, method: 'DELETE' }),
      invalidatesTags: (_result, _error, id) => [
        { type: 'MarketingSource', id: 'LIST' },
        { type: 'MarketingSource', id },
        'UiSchema',
        { type: 'Dashboard' as const, id: 'MAIN' },
        { type: 'Report' as const, id: 'CRM' },
      ],
    }),
  }),
});

function normalizeMarketingSourcePayload(body: Partial<MarketingSource>): Partial<MarketingSource> {
  const parsedSortOrder = body.sortOrder === undefined || body.sortOrder === null || body.sortOrder === ''
    ? undefined
    : Number(body.sortOrder);
  const sortOrder = parsedSortOrder === undefined || Number.isFinite(parsedSortOrder) ? parsedSortOrder : undefined;

  return {
    ...body,
    sortOrder,
  };
}

export const {
  useSearchMarketingSourcesQuery,
  useSaveMarketingSourceMutation,
  useArchiveMarketingSourceMutation,
} = marketingApi;
