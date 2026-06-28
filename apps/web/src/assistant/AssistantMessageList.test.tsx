// Copyright (c) Khaled Shawki. All rights reserved.

import { renderToStaticMarkup } from 'react-dom/server';
import { describe, expect, it } from 'vitest';
import AssistantMessageList from './AssistantMessageList';
import type { AssistantMessage } from './assistantTypes';

describe('AssistantMessageList', () => {
  it('shows an accessible assistant answering indicator while a request is pending', () => {
    const markup = renderToStaticMarkup(<AssistantMessageList messages={[]} isAssistantAnswering />);

    expect(markup).toContain('aria-label="Assistant is answering"');
    expect(markup).toContain('<output');
    expect(markup).toContain('Checking CRM data and preparing an answer');
  });

  it('keeps the optimistic user message visible while the assistant is answering', () => {
    const messages: AssistantMessage[] = [
      {
        id: -1,
        role: 'USER',
        content: 'Which leads need follow-up?',
        references: [],
        createdAt: '2026-06-26T08:00:00Z',
      },
    ];

    const markup = renderToStaticMarkup(<AssistantMessageList messages={messages} isAssistantAnswering />);

    expect(markup).toContain('Which leads need follow-up?');
    expect(markup).toContain('Checking CRM data and preparing an answer');
  });

  it('does not show the empty state while the assistant is answering', () => {
    const markup = renderToStaticMarkup(<AssistantMessageList messages={[]} isAssistantAnswering />);

    expect(markup).not.toContain('Ask a CRM-specific question');
  });
});
