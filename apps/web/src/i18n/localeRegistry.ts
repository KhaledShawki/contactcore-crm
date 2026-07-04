// Copyright (c) Khaled Shawki. All rights reserved.

export type SupportedLocale = 'en' | 'de' | 'ar';
export type TextDirection = 'ltr' | 'rtl';

export interface LocaleDefinition {
  locale: SupportedLocale;
  label: string;
  nativeLabel: string;
  direction: TextDirection;
  htmlLang: string;
}

export const defaultLocale: SupportedLocale = 'en';

export const supportedLocales: Record<SupportedLocale, LocaleDefinition> = {
  en: { locale: 'en', label: 'English', nativeLabel: 'English', direction: 'ltr', htmlLang: 'en' },
  de: { locale: 'de', label: 'German', nativeLabel: 'Deutsch', direction: 'ltr', htmlLang: 'de' },
  ar: { locale: 'ar', label: 'Arabic', nativeLabel: 'العربية', direction: 'rtl', htmlLang: 'ar' },
};

export function normalizeLocale(value: string | null | undefined): SupportedLocale {
  if (!value) return defaultLocale;
  const language = value.trim().replace('_', '-').split('-')[0]?.toLowerCase();
  return isSupportedLocale(language) ? language : defaultLocale;
}

function isSupportedLocale(value: string | null | undefined): value is SupportedLocale {
  return value === 'en' || value === 'de' || value === 'ar';
}

export function directionFor(locale: string | null | undefined): TextDirection {
  return supportedLocales[normalizeLocale(locale)].direction;
}

export function localeDefinition(locale: string | null | undefined): LocaleDefinition {
  return supportedLocales[normalizeLocale(locale)];
}
