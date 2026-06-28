// Copyright (c) Khaled Shawki. All rights reserved.

import { baseApi } from '../api/baseApi';
import type { AuthResponse, CurrentUser, LoginRequest } from './authTypes';

const authApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    login: builder.mutation<AuthResponse, LoginRequest>({
      query: (body) => ({ url: '/auth/login', method: 'POST', body }),
      invalidatesTags: ['Auth'],
    }),
    me: builder.query<CurrentUser, void>({
      query: () => '/auth/me',
      providesTags: ['Auth'],
    }),
  }),
});

export const { useLoginMutation, useMeQuery } = authApi;
