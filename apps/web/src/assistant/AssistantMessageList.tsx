// Copyright (c) Khaled Shawki. All rights reserved.

import { useEffect, useRef } from 'react';
import BlueAlert from '../components/BlueAlert';
import AssistantReferenceList from './AssistantReferenceList';
import type { AssistantMessage, AssistantResponse } from './assistantTypes';

interface Props {
  messages: AssistantMessage[];
  isAssistantAnswering?: boolean;
  latestResponse?: AssistantResponse | null;
}

function assistantMeta(message: AssistantMessage): string | null {
  if (!message.modelName && !message.answerSource && !message.answerStatus) {
    return null;
  }

  const parts = [
    message.modelName ? `Model: ${message.modelName}` : null,
    message.answerSource ? `Source: ${message.answerSource.toLowerCase()}` : null,
    message.answerStatus && message.answerStatus !== 'SUCCESS' ? `Status: ${message.answerStatus.toLowerCase()}` : null,
  ].filter(Boolean);

  return parts.join(' · ');
}

interface AssistantMessageContentProps {
  message: AssistantMessage;
}

function AssistantMessageContent({ message }: AssistantMessageContentProps) {
  const meta = assistantMeta(message);
  return (
    <>
      {message.warning && <BlueAlert tone="warning" message={message.warning} />}
      <p>{message.content}</p>
      <AssistantReferenceList references={message.references} />
      {meta && <small>{meta}</small>}
    </>
  );
}

function AssistantAnsweringIndicator() {
  return (
    <article className="assistant-message assistant-message--assistant assistant-message--pending" aria-label="Assistant is answering">
      <span>Assistant</span>
      <output className="assistant-thinking" aria-live="polite">
        <span className="assistant-thinking__dot" aria-hidden="true" />
        <span className="assistant-thinking__dot" aria-hidden="true" />
        <span className="assistant-thinking__dot" aria-hidden="true" />
        <span className="assistant-thinking__label">Checking CRM data and preparing an answer…</span>
      </output>
    </article>
  );
}

export default function AssistantMessageList({ messages, isAssistantAnswering = false, latestResponse = null }: Props) {
  const endRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
  }, [messages.length, isAssistantAnswering, latestResponse]);

  if (messages.length === 0 && !latestResponse && !isAssistantAnswering) {
    return (
      <div className="assistant-empty-state">
        <h2>Ask a CRM-specific question</h2>
        <p>Use the assistant to summarize CRM data, identify leads needing follow-up, or inspect marketing-source performance.</p>
      </div>
    );
  }

  return (
    <div className="assistant-message-list" aria-live="polite" aria-busy={isAssistantAnswering}>
      {messages.map((message) => (
        <article key={message.id} className={`assistant-message assistant-message--${message.role.toLowerCase()}`}>
          <span>{message.role === 'USER' ? 'You' : 'Assistant'}</span>
          {message.role === 'ASSISTANT' ? <AssistantMessageContent message={message} /> : <p>{message.content}</p>}
        </article>
      ))}
      {latestResponse && (
        <article className="assistant-message assistant-message--assistant">
          <span>Assistant</span>
          <AssistantMessageContent
            message={{
              id: -1,
              role: 'ASSISTANT',
              content: latestResponse.answer,
              references: latestResponse.references,
              createdAt: latestResponse.createdAt,
              answerStatus: latestResponse.answerStatus,
              answerSource: latestResponse.answerSource,
              warning: latestResponse.warning,
              modelName: latestResponse.modelName,
            }}
          />
        </article>
      )}
      {isAssistantAnswering && <AssistantAnsweringIndicator />}
      <div ref={endRef} aria-hidden="true" />
    </div>
  );
}
