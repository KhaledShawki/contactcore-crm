// Copyright (c) Khaled Shawki. All rights reserved.

import { baseApi } from '../api/baseApi';
import type {
  AssistantConversationDetail,
  AssistantConversationSummary,
  AssistantRequest,
  AssistantResponse,
} from './assistantTypes';

const assistantApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    sendAssistantMessage: builder.mutation<AssistantResponse, AssistantRequest>({
      query: (body) => ({
        url: '/assistant/messages',
        method: 'POST',
        body,
      }),
      invalidatesTags: ['Assistant'],
    }),
    getAssistantConversations: builder.query<AssistantConversationSummary[], void>({
      query: () => '/assistant/conversations',
      providesTags: ['Assistant'],
    }),
    getAssistantConversation: builder.query<AssistantConversationDetail, number>({
      query: (id) => `/assistant/conversations/${id}`,
      providesTags: (_result, _error, id) => [{ type: 'Assistant', id }],
    }),
    archiveAssistantConversation: builder.mutation<void, number>({
      query: (id) => ({
        url: `/assistant/conversations/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Assistant'],
    }),
  }),
});

export const {
  useSendAssistantMessageMutation,
  useGetAssistantConversationsQuery,
  useGetAssistantConversationQuery,
  useArchiveAssistantConversationMutation,
} = assistantApi;
