// Copyright (c) Khaled Shawki. All rights reserved.

import { dictionaries } from './locales';
import { defaultLocale, normalizeLocale } from './localeRegistry';

type Params = Record<string, string | number | boolean | null | undefined>;

export function translate(locale: string | null | undefined, key: string, params?: Params): string {
  const normalized = normalizeLocale(locale);
  const value = dictionaries[normalized][key] ?? dictionaries[defaultLocale][key] ?? key;
  return interpolate(value, params);
}


function interpolate(template: string, params: Params = {}): string {
  return template.replace(/{{\s*([\w.]+)\s*}}/g, (_match, name: string) => {
    const value = params[name];
    return value === null || value === undefined ? '' : String(value);
  });
}
