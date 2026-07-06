// Copyright (c) Khaled Shawki. All rights reserved.

export function translatedLabel(key: string | null | undefined, fallback: string | null | undefined, t: (key: string) => string): string {
  if (key) {
    const translated = t(key);
    if (translated !== key) return translated;
  }
  return fallback ?? '';
}

export function translateDataLabel(value: string | null | undefined, t: (key: string) => string): string {
  if (!value) return '';
  const normalized = value.trim().toUpperCase().replace(/\s+/g, '_');
  const key = `data.label.${normalized}`;
  const translated = t(key);
  return translated === key ? value : translated;
}
