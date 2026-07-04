// Copyright (c) Khaled Shawki. All rights reserved.

import { renderToStaticMarkup } from 'react-dom/server';
import { describe, expect, it, vi } from 'vitest';
import LanguageSelector from './LanguageSelector';
import { translate } from './translate';

vi.mock('../auth/authSlice', () => ({
  setCurrentUser: vi.fn(),
}));

vi.mock('../notifications/useNotifications', () => ({
  useNotifications: () => ({ notifySuccess: vi.fn(), notifyError: vi.fn() }),
}));

vi.mock('../profile/profileApi', () => ({
  useUpdateProfileLocaleMutation: () => [vi.fn(), { isLoading: false }],
}));

vi.mock('../store/hooks', () => ({
  useAppDispatch: () => vi.fn(),
  useAppSelector: () => null,
}));

vi.mock('./LocaleProvider', () => ({
  useLocale: () => ({
    locale: 'ar',
    direction: 'rtl',
    setAnonymousLocale: vi.fn(),
    t: (key: string, params?: Record<string, string | number | boolean | null | undefined>) => translate('ar', key, params),
  }),
}));

describe('LanguageSelector', () => {
  it('renders native language names with an RTL-safe control structure', () => {
    const markup = renderToStaticMarkup(<LanguageSelector />);

    expect(markup).toContain('class="language-selector"');
    expect(markup).toContain('data-direction="rtl"');
    expect(markup).toContain('English');
    expect(markup).toContain('Deutsch');
    expect(markup).toContain('العربية');
    expect(markup).toContain('class="language-selector__chevron"');
  });
});
