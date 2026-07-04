// Copyright (c) Khaled Shawki. All rights reserved.

import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import type { RootState } from '../store/store';

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? '/api';

export const baseApi = createApi({
  reducerPath: 'contactCoreApi',
  baseQuery: fetchBaseQuery({
    baseUrl: apiBaseUrl,
    prepareHeaders: (headers, { getState }) => {
      const state = getState() as RootState;
      const token = state.auth.accessToken;
      const locale = state.auth.user?.locale;
      if (locale) {
        headers.set('accept-language', locale);
      }
      if (token) {
        headers.set('authorization', `Bearer ${token}`);
      }
      return headers;
    },
  }),
  tagTypes: ['Auth', 'UiSchema', 'BusinessPartner', 'ContactPerson', 'Profile', 'ProfileImage', 'Documents', 'MarketingSource', 'Settings', 'Dashboard', 'Report', 'Assistant', 'Connector', 'ConnectorSession', 'ConnectorBusinessPartner'],
  endpoints: () => ({}),
});
