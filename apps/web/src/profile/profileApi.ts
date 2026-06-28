// Copyright (c) Khaled Shawki. All rights reserved.

import { baseApi } from '../api/baseApi';
import type { UserProfile } from '../schema/types';

const API_PREFIX = '/api';

export function profileImageContentEndpoint(profileImageUrl: string): string {
  const value = profileImageUrl.trim();
  if (!value) return '/profile/image/content';
  if (value.startsWith(`${API_PREFIX}/`)) return value.slice(API_PREFIX.length);
  return value;
}

const profileApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getProfile: builder.query<UserProfile, void>({
      query: () => '/profile',
      providesTags: [{ type: 'Profile' as const, id: 'ME' }],
    }),
    updateProfile: builder.mutation<UserProfile, Partial<UserProfile>>({
      query: (body) => ({ url: '/profile', method: 'PUT', body }),
      invalidatesTags: [{ type: 'Profile' as const, id: 'ME' }, 'Auth'],
    }),
    uploadProfileImage: builder.mutation<UserProfile, File>({
      query: (file) => {
        const body = new FormData();
        body.append('file', file);
        return { url: '/profile/image', method: 'POST', body };
      },
      invalidatesTags: (result) => [
        { type: 'Profile' as const, id: 'ME' },
        { type: 'ProfileImage' as const, id: 'ME' },
        ...(result?.profileImageUrl ? [{ type: 'ProfileImage' as const, id: result.profileImageUrl }] : []),
      ],
    }),
    getProfileImage: builder.query<string, string>({
      query: (profileImageUrl) => ({
        url: profileImageContentEndpoint(profileImageUrl),
        responseHandler: (response) => response.blob(),
      }),
      transformResponse: (blob: Blob) => URL.createObjectURL(blob),
      async onCacheEntryAdded(_profileImageUrl, { cacheDataLoaded, cacheEntryRemoved }) {
        let objectUrl: string | null = null;
        try {
          const cached = await cacheDataLoaded;
          objectUrl = cached.data;
        } finally {
          await cacheEntryRemoved;
          if (objectUrl) {
            URL.revokeObjectURL(objectUrl);
          }
        }
      },
      providesTags: (_result, _error, profileImageUrl) => [
        { type: 'ProfileImage' as const, id: 'ME' },
        { type: 'ProfileImage' as const, id: profileImageUrl },
      ],
    }),
  }),
});

export const { useGetProfileQuery, useUpdateProfileMutation, useUploadProfileImageMutation, useGetProfileImageQuery } = profileApi;
