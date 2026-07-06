// Copyright (c) Khaled Shawki. All rights reserved.

export type DataSourceValues = Record<string, unknown>;

export function readPath(source: unknown, path: string | null | undefined): unknown {
  if (!path) return source;
  return path.split('.').filter(Boolean).reduce<unknown>((current, segment) => {
    if (current == null || typeof current !== 'object') return undefined;
    return (current as Record<string, unknown>)[segment];
  }, source);
}

export function asArray<T = Record<string, unknown>>(value: unknown): T[] {
  return Array.isArray(value) ? value as T[] : [];
}

export function readString(row: Record<string, unknown>, key: string | null | undefined): string {
  if (!key) return '';
  const value = row[key];
  return value == null ? '' : String(value);
}

export function readNumber(row: Record<string, unknown>, key: string | null | undefined): number {
  if (!key) return 0;
  const value = row[key];
  if (typeof value === 'number') return Number.isFinite(value) ? value : 0;
  if (typeof value === 'string' && value.trim() !== '') {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : 0;
  }
  return 0;
}
