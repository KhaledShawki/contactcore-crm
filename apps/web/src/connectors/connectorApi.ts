// Copyright (c) Khaled Shawki. All rights reserved.

import { baseApi } from '../api/baseApi';
import type { BusinessPartner } from '../schema/types';
import type { ConnectorBusinessPartnerPage, ConnectorInstance, ConnectorLoginRequest, ConnectorSessionStatus } from './connectorTypes';

interface SearchConnectorBusinessPartnersArgs {
  type: string;
  query: string;
  page: number;
  size: number;
  sort?: string;
}

const connectorApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    listConnectorInstances: builder.query<ConnectorInstance[], void>({
      query: () => '/connectors/instances',
      providesTags: ['Connector'],
    }),
    getConnectorSession: builder.query<ConnectorSessionStatus, void>({
      query: () => '/connectors/session',
      providesTags: ['ConnectorSession'],
    }),
    loginConnectorSession: builder.mutation<ConnectorSessionStatus, ConnectorLoginRequest>({
      query: (body) => ({ url: '/connectors/session', method: 'POST', body }),
      invalidatesTags: ['ConnectorSession', { type: 'ConnectorBusinessPartner', id: 'LIST' }],
    }),
    disconnectConnectorSession: builder.mutation<void, void>({
      query: () => ({ url: '/connectors/session', method: 'DELETE' }),
      invalidatesTags: ['ConnectorSession', { type: 'ConnectorBusinessPartner', id: 'LIST' }],
    }),
    searchConnectorBusinessPartners: builder.query<ConnectorBusinessPartnerPage, SearchConnectorBusinessPartnersArgs>({
      query: ({ type, query, page, size, sort = 'code_asc' }) => `/connectors/business-partners?type=${encodeURIComponent(type)}&q=${encodeURIComponent(query)}&page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`,
      providesTags: (result) => [
        { type: 'ConnectorBusinessPartner', id: 'LIST' },
        ...(result?.items.map((item) => ({ type: 'ConnectorBusinessPartner' as const, id: item.externalId ?? item.code })) ?? []),
      ],
    }),
    getConnectorBusinessPartner: builder.query<BusinessPartner, string>({
      query: (externalId) => `/connectors/business-partners/${encodeURIComponent(externalId)}`,
      providesTags: (_result, _error, externalId) => [{ type: 'ConnectorBusinessPartner', id: externalId }],
    }),
  }),
});

export const {
  useListConnectorInstancesQuery,
  useGetConnectorSessionQuery,
  useLoginConnectorSessionMutation,
  useDisconnectConnectorSessionMutation,
  useSearchConnectorBusinessPartnersQuery,
  useGetConnectorBusinessPartnerQuery,
} = connectorApi;
