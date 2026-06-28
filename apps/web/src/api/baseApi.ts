// Copyright (c) Khaled Shawki. All rights reserved.

import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import type { RootState } from '../store/store';

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? '/api';

export const baseApi = createApi({
  reducerPath: 'contactCoreApi',
  baseQuery: fetchBaseQuery({
    baseUrl: apiBaseUrl,
    prepareHeaders: (headers, { getState }) => {
      const token = (getState() as RootState).auth.accessToken;
      if (token) {
        headers.set('authorization', `Bearer ${token}`);
      }
      return headers;
    },
  }),
  tagTypes: ['Auth', 'UiSchema', 'BusinessPartner', 'ContactPerson', 'Profile', 'ProfileImage', 'Documents', 'MarketingSource', 'Settings', 'Dashboard', 'Report', 'Assistant'],
  endpoints: () => ({}),
});
