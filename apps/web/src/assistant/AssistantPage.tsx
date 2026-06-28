// Copyright (c) Khaled Shawki. All rights reserved.

import { useCallback, useMemo, useState } from 'react';
import BlueAlert from '../components/BlueAlert';
import BlueButton from '../components/BlueButton';
import AssistantConversationSidebar from './AssistantConversationSidebar';
import AssistantMessageInput from './AssistantMessageInput';
import AssistantMessageList from './AssistantMessageList';
import type { AssistantMessage, AssistantResponse } from './assistantTypes';
import {
  useArchiveAssistantConversationMutation,
  useGetAssistantConversationQuery,
  useGetAssistantConversationsQuery,
  useSendAssistantMessageMutation,
} from './assistantApi';

const suggestedPrompts = [
  'Which leads need follow-up?',
  'Which leads are missing contact persons?',
  'Show marketing source performance.',
  'Summarize the CRM status.',
  'Find records related to Meyer.',
];

interface PendingAssistantExchange {
  conversationId: number | null;
  baseMessageCount: number;
  messages: AssistantMessage[];
}

function createOptimisticUserMessage(content: string): AssistantMessage {
  return {
    id: -Date.now(),
    role: 'USER',
    content,
    references: [],
    createdAt: new Date().toISOString(),
  };
}

function createOptimisticAssistantMessage(response: AssistantResponse): AssistantMessage {
  return {
    id: -Date.now() - 1,
    role: 'ASSISTANT',
    content: response.answer,
    references: response.references,
    createdAt: response.createdAt,
    answerStatus: response.answerStatus,
    answerSource: response.answerSource,
    warning: response.warning,
    modelName: response.modelName,
  };
}

function extractRequestErrorMessage(error: unknown): string {
  if (typeof error === 'object' && error !== null && 'data' in error) {
    const data = (error as { data?: unknown }).data;
    if (typeof data === 'object' && data !== null && 'message' in data) {
      const message = (data as { message?: unknown }).message;
      if (typeof message === 'string' && message.trim()) {
        return message;
      }
    }
  }

  return 'Assistant request failed. Check the backend logs for details.';
}

export default function AssistantPage() {
  const [activeConversationId, setActiveConversationId] = useState<number | null>(null);
  const [pendingExchange, setPendingExchange] = useState<PendingAssistantExchange | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [composerKey, setComposerKey] = useState(0);

  const { data: conversations = [], isLoading: conversationsLoading } = useGetAssistantConversationsQuery();
  const { data: activeConversation, isFetching: conversationLoading } = useGetAssistantConversationQuery(activeConversationId as number, {
    skip: activeConversationId === null,
  });
  const [sendMessage, { isLoading: isSending }] = useSendAssistantMessageMutation();
  const [archiveConversation, { isLoading: isArchiving }] = useArchiveAssistantConversationMutation();

  const selectedConversation = useMemo(() => {
    if (activeConversationId === null || activeConversation?.id !== activeConversationId) {
      return null;
    }

    return activeConversation;
  }, [activeConversation, activeConversationId]);

  const pendingMessages = useMemo(() => {
    if (!pendingExchange || pendingExchange.conversationId !== activeConversationId) {
      return [];
    }

    if (selectedConversation && selectedConversation.messages.length > pendingExchange.baseMessageCount) {
      return [];
    }

    return pendingExchange.messages;
  }, [activeConversationId, pendingExchange, selectedConversation]);

  const displayedMessages = useMemo(() => {
    if (selectedConversation) {
      return [...selectedConversation.messages, ...pendingMessages];
    }
    return pendingMessages;
  }, [selectedConversation, pendingMessages]);

  const resetComposer = useCallback(() => {
    setComposerKey((current) => current + 1);
  }, []);

  const startNewConversation = useCallback(() => {
    setActiveConversationId(null);
    setPendingExchange(null);
    setError(null);
    resetComposer();
  }, [resetComposer]);

  const selectConversation = (conversationId: number) => {
    setActiveConversationId(conversationId);
    setPendingExchange(null);
    setError(null);
    resetComposer();
  };

  const removeConversation = async (conversationId: number) => {
    setError(null);
    try {
      await archiveConversation(conversationId).unwrap();
      if (conversationId === activeConversationId) {
        startNewConversation();
      }
    } catch {
      setError('Could not archive this conversation. Try again.');
    }
  };

  const ask = async (message: string) => {
    const requestConversationId = activeConversationId;
    const baseMessageCount = selectedConversation?.messages.length ?? 0;
    const userMessage = createOptimisticUserMessage(message);

    setError(null);
    setPendingExchange({
      conversationId: requestConversationId,
      baseMessageCount,
      messages: [userMessage],
    });

    try {
      const response = await sendMessage({ conversationId: requestConversationId, message }).unwrap();
      const assistantMessage = createOptimisticAssistantMessage(response);
      setActiveConversationId(response.conversationId);
      setPendingExchange({
        conversationId: response.conversationId,
        baseMessageCount,
        messages: [userMessage, assistantMessage],
      });
    } catch (requestError) {
      setError(extractRequestErrorMessage(requestError));
    }
  };

  const showSuggestions = displayedMessages.length === 0;

  return (
    <div className="assistant-chat-shell">
      <AssistantConversationSidebar
        activeConversationId={activeConversationId}
        conversations={conversations}
        isLoading={conversationsLoading}
        onNewConversation={startNewConversation}
        onSelectConversation={selectConversation}
        onArchiveConversation={removeConversation}
      />

      <section className="assistant-chat-main" aria-label="ContactCore Assistant chat">
        <header className="assistant-chat-header">
          <div>
            <p className="eyebrow">AI-assisted CRM</p>
            <h1>ContactCore Assistant</h1>
            <p className="muted-text">
              Ask focused CRM questions. Answers are generated from authorized ContactCore data and include record references when available.
            </p>
          </div>
          <BlueButton type="button" variant="secondary" onClick={startNewConversation}>New conversation</BlueButton>
        </header>

        {error && <BlueAlert message={error} />}

        {showSuggestions && (
          <div className="assistant-suggestions" aria-label="Suggested assistant prompts">
            {suggestedPrompts.map((prompt) => (
              <BlueButton key={prompt} variant="secondary" disabled={isSending} onClick={() => ask(prompt)}>
                {prompt}
              </BlueButton>
            ))}
          </div>
        )}

        {activeConversationId !== null && conversationLoading && displayedMessages.length === 0 && (
          <div className="assistant-empty-state">
            <h2>Loading conversation</h2>
            <p>Retrieving the selected assistant conversation.</p>
          </div>
        )}

        <AssistantMessageList messages={displayedMessages} isAssistantAnswering={isSending} />

        <div className="assistant-chat-composer">
          <AssistantMessageInput key={composerKey} disabled={isSending || isArchiving} onSubmit={ask} />
        </div>
      </section>
    </div>
  );
}
