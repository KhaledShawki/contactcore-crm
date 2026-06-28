// Copyright (c) Khaled Shawki. All rights reserved.

import type { UiField, UiScreen } from './types';

export type SchemaRecordValue = string | number | boolean | null | undefined;
export type SchemaRecord = Record<string, SchemaRecordValue>;

export function createDefaultRecord(screen: UiScreen): SchemaRecord {
  return screen.fields.reduce<SchemaRecord>((draft, field) => {
    if (field.type === 'checkbox') {
      draft[field.key] = field.defaultValue === 'true';
      return draft;
    }
    if (field.defaultValue !== null && field.defaultValue !== undefined) {
      draft[field.key] = field.defaultValue;
    }
    return draft;
  }, {});
}

export function toWritablePayload(screen: UiScreen, value: SchemaRecord): SchemaRecord {
  return screen.fields.reduce<SchemaRecord>((payload, field) => {
    if (field.readOnly) {
      return payload;
    }
    const nextValue = normalizeFieldValue(field, value[field.key]);
    if (field.required || nextValue !== null) {
      payload[field.key] = nextValue;
    }
    return payload;
  }, {});
}

function normalizeFieldValue(field: UiField, value: SchemaRecordValue): string | number | boolean | null {
  if (field.type === 'checkbox') {
    return Boolean(value);
  }
  if (value === undefined || value === null) {
    return defaultValueFor(field);
  }
  if (typeof value === 'boolean') {
    return value;
  }
  if (typeof value === 'number') {
    return Number.isFinite(value) ? value : defaultValueFor(field);
  }
  const trimmed = value.trim();
  if (trimmed.length === 0) {
    return defaultValueFor(field);
  }
  if (field.type === 'number' || field.validation?.inputType === 'number') {
    const parsed = Number(trimmed);
    return Number.isFinite(parsed) ? parsed : trimmed;
  }
  return trimmed;
}

function defaultValueFor(field: UiField): string | number | boolean | null {
  if (field.type === 'checkbox') {
    return field.defaultValue === 'true';
  }
  if (field.type === 'number' || field.validation?.inputType === 'number') {
    if (field.defaultValue === null || field.defaultValue === undefined || field.defaultValue === '') {
      return null;
    }
    const parsed = Number(field.defaultValue);
    return Number.isFinite(parsed) ? parsed : null;
  }
  return field.defaultValue ?? null;
}
