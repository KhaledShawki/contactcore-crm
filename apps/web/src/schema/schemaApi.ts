// Copyright (c) Khaled Shawki. All rights reserved.

import { baseApi } from '../api/baseApi';
import type { UiManifest, UiScreen } from './types';

const schemaApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getManifest: builder.query<UiManifest, void>({
      query: () => '/ui/manifest',
      providesTags: ['UiSchema'],
    }),
    getScreen: builder.query<UiScreen, string>({
      query: (screenKey) => `/ui/screens/${screenKey}`,
      providesTags: (_result, _error, screenKey) => [{ type: 'UiSchema', id: screenKey }],
    }),
  }),
});

export const { useGetManifestQuery, useGetScreenQuery } = schemaApi;
