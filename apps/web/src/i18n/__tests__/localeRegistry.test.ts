// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { directionFor, normalizeLocale } from '../localeRegistry';

 describe('locale registry', () => {
  it('normalizes supported language tags', () => {
    expect(normalizeLocale('de-DE')).toBe('de');
    expect(normalizeLocale('ar_EG')).toBe('ar');
    expect(normalizeLocale('en-US')).toBe('en');
  });

  it('falls back to English for unsupported locales', () => {
    expect(normalizeLocale('fr-FR')).toBe('en');
  });

  it('resolves Arabic as RTL and German as LTR', () => {
    expect(directionFor('ar')).toBe('rtl');
    expect(directionFor('de')).toBe('ltr');
  });
});
