// Copyright (c) Khaled Shawki. All rights reserved.

import { createContext, use, useEffect, useMemo, useState, type ReactNode } from 'react';
import { useAppSelector } from '../store/hooks';
import { defaultLocale, localeDefinition, normalizeLocale, type SupportedLocale, type TextDirection } from './localeRegistry';
import { translate } from './translate';

const ANONYMOUS_LOCALE_KEY = 'contactcore.locale';

interface LocaleContextValue {
  locale: SupportedLocale;
  direction: TextDirection;
  setAnonymousLocale: (locale: SupportedLocale) => void;
  t: (key: string, params?: Record<string, string | number | boolean | null | undefined>) => string;
}

const LocaleContext = createContext<LocaleContextValue | null>(null);

export function LocaleProvider({ children }: { children: ReactNode }) {
  const userLocale = useAppSelector((state) => state.auth.user?.locale);
  const [anonymousLocale, setAnonymousLocaleState] = useState<SupportedLocale>(() => resolveAnonymousLocale());
  const locale = normalizeLocale(userLocale ?? anonymousLocale);
  const definition = localeDefinition(locale);

  useEffect(() => {
    document.documentElement.lang = definition.htmlLang;
    document.documentElement.dir = definition.direction;
    document.documentElement.dataset.locale = definition.locale;
  }, [definition]);

  const value = useMemo<LocaleContextValue>(() => ({
    locale,
    direction: definition.direction,
    setAnonymousLocale(nextLocale) {
      localStorage.setItem(ANONYMOUS_LOCALE_KEY, nextLocale);
      setAnonymousLocaleState(nextLocale);
    },
    t(key, params) {
      return translate(locale, key, params);
    },
  }), [definition.direction, locale]);

  return <LocaleContext.Provider value={value}>{children}</LocaleContext.Provider>;
}

export function useLocale() {
  const value = use(LocaleContext);
  if (!value) {
    throw new Error('useLocale must be used inside LocaleProvider.');
  }
  return value;
}

function resolveAnonymousLocale(): SupportedLocale {
  const stored = localStorage.getItem(ANONYMOUS_LOCALE_KEY);
  if (stored) return normalizeLocale(stored);
  const browserLocale = navigator.languages?.[0] ?? navigator.language;
  return normalizeLocale(browserLocale ?? defaultLocale);
}
