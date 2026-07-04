// Copyright (c) Khaled Shawki. All rights reserved.

import { normalizeLocale, type SupportedLocale } from './localeRegistry';

const dateTimeFormatters: Record<SupportedLocale, Intl.DateTimeFormat> = {
  en: new Intl.DateTimeFormat('en', { dateStyle: 'medium', timeStyle: 'short' }),
  de: new Intl.DateTimeFormat('de', { dateStyle: 'medium', timeStyle: 'short' }),
  ar: new Intl.DateTimeFormat('ar', { dateStyle: 'medium', timeStyle: 'short' }),
};

const numberFormatters: Record<SupportedLocale, Intl.NumberFormat> = {
  en: new Intl.NumberFormat('en'),
  de: new Intl.NumberFormat('de'),
  ar: new Intl.NumberFormat('ar'),
};

export function formatDateTime(value: string | number | Date | null | undefined, locale: string): string {
  const date = toDate(value);
  return date ? dateTimeFormatters[normalizeLocale(locale)].format(date) : '';
}

export function formatNumber(value: string | number | null | undefined, locale: string): string {
  const number = toNumber(value);
  return number === null ? '' : numberFormatters[normalizeLocale(locale)].format(number);
}

function toDate(value: string | number | Date | null | undefined): Date | null {
  if (value === null || value === undefined || value === '') return null;
  const date = value instanceof Date ? value : new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
}

function toNumber(value: string | number | null | undefined): number | null {
  if (value === null || value === undefined || value === '') return null;
  const number = typeof value === 'number' ? value : Number(value);
  return Number.isFinite(number) ? number : null;
}
