// Copyright (c) Khaled Shawki. All rights reserved.

import { baseApi } from '../api/baseApi';
import type { BusinessPartner, ContactPerson, DocumentAttachment, PageResponse } from '../schema/types';

interface SearchArgs {
  kind: string;
  query: string;
  page: number;
  size: number;
  sort?: string;
}

interface SaveArgs {
  id?: number;
  body: Partial<BusinessPartner>;
}

interface UploadDocumentArgs {
  businessPartnerId: number;
  file: File;
  documentTypeCode?: string;
}

interface SaveContactPersonArgs {
  businessPartnerId: number;
  id?: number;
  body: ContactPerson;
}

const crmApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    searchBusinessPartners: builder.query<PageResponse<BusinessPartner>, SearchArgs>({
      query: ({ kind, query, page, size, sort = 'updated_desc' }) => `/crm/business-partners?kind=${encodeURIComponent(kind)}&q=${encodeURIComponent(query)}&page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`,
      providesTags: (result) => [
        { type: 'BusinessPartner', id: 'LIST' },
        ...(result?.items.map((item) => ({ type: 'BusinessPartner' as const, id: item.id })) ?? []),
      ],
    }),
    getBusinessPartner: builder.query<BusinessPartner, number>({
      query: (id) => `/crm/business-partners/${id}`,
      providesTags: (_result, _error, id) => [{ type: 'BusinessPartner', id }],
    }),
    saveBusinessPartner: builder.mutation<BusinessPartner, SaveArgs>({
      query: ({ id, body }) => ({
        url: id ? `/crm/business-partners/${id}` : '/crm/business-partners',
        method: id ? 'PUT' : 'POST',
        body,
      }),
      invalidatesTags: (_result, _error, args) => [
        { type: 'BusinessPartner', id: 'LIST' },
        { type: 'Dashboard' as const, id: 'MAIN' },
        { type: 'Report' as const, id: 'CRM' },
        ...(args.id ? [{ type: 'BusinessPartner' as const, id: args.id }] : []),
      ],
    }),
    archiveBusinessPartner: builder.mutation<void, number>({
      query: (id) => ({ url: `/crm/business-partners/${id}`, method: 'DELETE' }),
      invalidatesTags: (_result, _error, id) => [
        { type: 'BusinessPartner', id: 'LIST' },
        { type: 'BusinessPartner', id },
        { type: 'Dashboard' as const, id: 'MAIN' },
        { type: 'Report' as const, id: 'CRM' },
      ],
    }),

    listContactPersons: builder.query<ContactPerson[], number>({
      query: (businessPartnerId) => `/crm/business-partners/${businessPartnerId}/contact-persons`,
      providesTags: (_result, _error, businessPartnerId) => [{ type: 'ContactPerson', id: businessPartnerId }],
    }),
    saveContactPerson: builder.mutation<ContactPerson, SaveContactPersonArgs>({
      query: ({ businessPartnerId, id, body }) => ({
        url: id ? `/crm/business-partners/${businessPartnerId}/contact-persons/${id}` : `/crm/business-partners/${businessPartnerId}/contact-persons`,
        method: id ? 'PUT' : 'POST',
        body,
      }),
      invalidatesTags: (_result, _error, args) => [
        { type: 'ContactPerson', id: args.businessPartnerId },
        { type: 'BusinessPartner', id: args.businessPartnerId },
        { type: 'Dashboard' as const, id: 'MAIN' },
        { type: 'Report' as const, id: 'CRM' },
      ],
    }),
    archiveContactPerson: builder.mutation<void, { businessPartnerId: number; id: number }>({
      query: ({ businessPartnerId, id }) => ({ url: `/crm/business-partners/${businessPartnerId}/contact-persons/${id}`, method: 'DELETE' }),
      invalidatesTags: (_result, _error, args) => [
        { type: 'ContactPerson', id: args.businessPartnerId },
        { type: 'BusinessPartner', id: args.businessPartnerId },
        { type: 'Dashboard' as const, id: 'MAIN' },
        { type: 'Report' as const, id: 'CRM' },
      ],
    }),
    listBusinessPartnerDocuments: builder.query<DocumentAttachment[], number>({
      query: (businessPartnerId) => `/crm/business-partners/${businessPartnerId}/documents`,
      providesTags: (_result, _error, businessPartnerId) => [{ type: 'Documents', id: businessPartnerId }],
    }),
    uploadBusinessPartnerDocument: builder.mutation<DocumentAttachment, UploadDocumentArgs>({
      query: ({ businessPartnerId, file, documentTypeCode = 'GENERAL' }) => {
        const body = new FormData();
        body.append('file', file);
        body.append('documentTypeCode', documentTypeCode);
        return { url: `/crm/business-partners/${businessPartnerId}/documents`, method: 'POST', body };
      },
      invalidatesTags: (_result, _error, args) => [{ type: 'Documents', id: args.businessPartnerId }],
    }),
    archiveBusinessPartnerDocument: builder.mutation<void, { businessPartnerId: number; documentId: number }>({
      query: ({ documentId }) => ({ url: `/crm/business-partners/documents/${documentId}`, method: 'DELETE' }),
      invalidatesTags: (_result, _error, args) => [{ type: 'Documents', id: args.businessPartnerId }],
    }),
  }),
});

export const {
  useSearchBusinessPartnersQuery,
  useGetBusinessPartnerQuery,
  useSaveBusinessPartnerMutation,
  useArchiveBusinessPartnerMutation,
  useListContactPersonsQuery,
  useSaveContactPersonMutation,
  useArchiveContactPersonMutation,
  useListBusinessPartnerDocumentsQuery,
  useUploadBusinessPartnerDocumentMutation,
  useArchiveBusinessPartnerDocumentMutation,
} = crmApi;
