// Copyright (c) Khaled Shawki. All rights reserved.

import { baseApi } from '../api/baseApi';
import type { UiSettings } from '../schema/types';

const settingsApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getUiSettings: builder.query<UiSettings, void>({
      query: () => '/settings/ui',
      providesTags: [{ type: 'Settings', id: 'UI' }],
    }),
    updateUiSettings: builder.mutation<UiSettings, UiSettings>({
      query: (body) => ({ url: '/settings/ui', method: 'PUT', body }),
      invalidatesTags: [{ type: 'Settings', id: 'UI' }],
    }),
  }),
});

export const { useGetUiSettingsQuery, useUpdateUiSettingsMutation } = settingsApi;
