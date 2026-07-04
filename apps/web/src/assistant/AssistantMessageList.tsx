// Copyright (c) Khaled Shawki. All rights reserved.

import { useEffect, useRef } from 'react';
import { useLocale } from '../i18n/LocaleProvider';
import AssistantMessageBubble from './AssistantMessageBubble';
import AssistantThinkingIndicator from './AssistantThinkingIndicator';
import type { AssistantMessage, AssistantResponse } from './assistantTypes';

interface Props {
  messages: AssistantMessage[];
  isAssistantAnswering?: boolean;
  latestResponse?: AssistantResponse | null;
}

function responseToMessage(response: AssistantResponse): AssistantMessage {
  return {
    id: -1,
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

export default function AssistantMessageList({ messages, isAssistantAnswering = false, latestResponse = null }: Props) {
  const { t } = useLocale();
  const endRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
  }, [messages.length, isAssistantAnswering, latestResponse]);

  if (messages.length === 0 && !latestResponse && !isAssistantAnswering) {
    return (
      <div className="assistant-empty-state">
        <h2>{t('assistant.empty.title')}</h2>
        <p>{t('assistant.empty.description')}</p>
      </div>
    );
  }

  return (
    <div className="assistant-message-list" aria-live="polite" aria-busy={isAssistantAnswering}>
      {messages.map((message) => <AssistantMessageBubble key={message.id} message={message} />)}
      {latestResponse && <AssistantMessageBubble message={responseToMessage(latestResponse)} />}
      {isAssistantAnswering && <AssistantThinkingIndicator />}
      <div ref={endRef} aria-hidden="true" />
    </div>
  );
}
