// Copyright (c) Khaled Shawki. All rights reserved.

export interface AssistantReference {
  entityType: string;
  entityId: number;
  label: string;
  route: string;
}

export interface AssistantRequest {
  conversationId?: number | null;
  message: string;
}

export type AssistantAnswerStatus = 'SUCCESS' | 'DEGRADED' | 'FAILED';
export type AssistantAnswerSource = 'DETERMINISTIC' | 'LLM' | 'FALLBACK';

export interface AssistantResponse {
  conversationId: number;
  answer: string;
  retrievalType: string;
  modelName: string;
  answerStatus: AssistantAnswerStatus;
  answerSource: AssistantAnswerSource;
  warning?: string | null;
  references: AssistantReference[];
  createdAt: string;
}

export interface AssistantConversationSummary {
  id: number;
  title: string;
  createdAt: string;
  updatedAt: string;
}

export interface AssistantMessage {
  id: number;
  role: 'USER' | 'ASSISTANT' | 'SYSTEM';
  content: string;
  references: AssistantReference[];
  createdAt: string;
  answerStatus?: AssistantAnswerStatus;
  answerSource?: AssistantAnswerSource;
  warning?: string | null;
  modelName?: string;
}

export interface AssistantConversationDetail extends AssistantConversationSummary {
  messages: AssistantMessage[];
}
