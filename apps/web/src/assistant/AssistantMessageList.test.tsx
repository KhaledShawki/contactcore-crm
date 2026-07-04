// Copyright (c) Khaled Shawki. All rights reserved.

import { renderToStaticMarkup } from 'react-dom/server';
import { describe, expect, it, vi } from 'vitest';
import AssistantMessageList from './AssistantMessageList';
import { translate } from '../i18n/translate';
import type { AssistantMessage } from './assistantTypes';

vi.mock('../i18n/LocaleProvider', () => ({
  useLocale: () => ({
    locale: 'en',
    direction: 'ltr',
    setAnonymousLocale: vi.fn(),
    t: (key: string, params?: Record<string, string | number | boolean | null | undefined>) => translate('en', key, params),
  }),
}));

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
