// Copyright (c) Khaled Shawki. All rights reserved.

import { renderToStaticMarkup } from 'react-dom/server';
import { describe, expect, it, vi } from 'vitest';
import NotificationViewport from './NotificationViewport';
import { translate } from '../i18n/translate';
import type { AppNotification } from './notificationTypes';

vi.mock('../i18n/LocaleProvider', () => ({
  useLocale: () => ({
    locale: 'en',
    direction: 'ltr',
    setAnonymousLocale: vi.fn(),
    t: (key: string, params?: Record<string, string | number | boolean | null | undefined>) => translate('en', key, params),
  }),
}));

const notification: AppNotification = {
  id: 'notification-1',
  tone: 'success',
  message: 'Customer saved.',
  createdAt: 1,
  dismissAfterMs: 4000,
};

describe('NotificationViewport', () => {
  it('renders no markup when there are no notifications', () => {
    expect(renderToStaticMarkup(<NotificationViewport notifications={[]} onDismiss={() => undefined} />)).toBe('');
  });

  it('renders notifications in an accessible live output region', () => {
    const markup = renderToStaticMarkup(<NotificationViewport notifications={[notification]} onDismiss={() => undefined} />);

    expect(markup).toContain('<output');
    expect(markup).toContain('aria-live="polite"');
    expect(markup).toContain('Application notifications');
    expect(markup).toContain('Customer saved.');
    expect(markup).toContain('Dismiss notification');
  });
});
