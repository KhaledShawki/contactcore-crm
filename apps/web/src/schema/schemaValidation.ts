// Copyright (c) Khaled Shawki. All rights reserved.

import type { SchemaRecord, SchemaRecordValue } from './schemaValues';
import type { UiField, UiScreen } from './types';

export interface SchemaValidationResult {
  valid: boolean;
  fieldErrors: Record<string, string>;
  formError: string | null;
}

export type SchemaTranslator = (key: string, params?: Record<string, string | number | boolean | null | undefined>) => string;

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const urlPattern = /^https?:\/\/.+/i;
const defaultTranslator: SchemaTranslator = (key, params) => {
  if (key.startsWith('schema.field.')) {
    return humanizeFieldKey(key.slice('schema.field.'.length));
  }
  switch (key) {
    case 'schema.validation.required': return `${params?.field ?? 'Field'} is required.`;
    case 'schema.validation.minLength': return `Minimum length is ${params?.count} characters.`;
    case 'schema.validation.maxLength': return `Maximum length is ${params?.count} characters.`;
    case 'schema.validation.email': return 'Enter a valid email address.';
    case 'schema.validation.url': return 'Enter a valid URL.';
    case 'schema.validation.number': return 'Enter a valid number.';
    case 'schema.validation.minNumber': return `Minimum value is ${params?.count}.`;
    case 'schema.validation.maxNumber': return `Maximum value is ${params?.count}.`;
    case 'schema.validation.invalidFormat': return 'Invalid format.';
    default: return key;
  }
};

export function validateSchemaRecord(screen: UiScreen, value: SchemaRecord, t: SchemaTranslator = defaultTranslator): SchemaValidationResult {
  const fieldErrors: Record<string, string> = {};

  for (const field of screen.fields) {
    if (!field.formVisible || field.readOnly) {
      continue;
    }
    const error = validateField(field, value[field.key], t);
    if (error) {
      fieldErrors[field.key] = error;
    }
  }

  const formError = validateFormRules(screen, value);

  return {
    valid: Object.keys(fieldErrors).length === 0 && formError === null,
    fieldErrors,
    formError,
  };
}

function validateField(field: UiField, value: SchemaRecordValue, t: SchemaTranslator): string | null {
  if (field.type === 'checkbox') {
    return null;
  }

  const textValue = normalizeText(value);
  const validation = field.validation;
  const isEmpty = textValue.length === 0;
  const fieldLabel = t(field.labelKey ?? `schema.field.${field.key}`);

  if (field.required && isEmpty) {
    return t('schema.validation.required', { field: fieldLabel });
  }
  if (isEmpty) {
    return null;
  }

  if (validation?.minLength !== null && validation?.minLength !== undefined && textValue.length < validation.minLength) {
    return t('schema.validation.minLength', { count: validation.minLength });
  }
  if (validation?.maxLength !== null && validation?.maxLength !== undefined && textValue.length > validation.maxLength) {
    return t('schema.validation.maxLength', { count: validation.maxLength });
  }

  const inputType = validation?.inputType ?? field.type;
  if (inputType === 'email' && !emailPattern.test(textValue)) {
    return t('schema.validation.email');
  }
  if (inputType === 'url' && !urlPattern.test(textValue)) {
    return validation?.patternMessage ?? t('schema.validation.url');
  }
  if (inputType === 'number' || field.type === 'number') {
    const parsed = Number(textValue);
    if (!Number.isFinite(parsed)) {
      return t('schema.validation.number');
    }
    if (validation?.minNumber !== null && validation?.minNumber !== undefined && parsed < validation.minNumber) {
      return t('schema.validation.minNumber', { count: validation.minNumber });
    }
    if (validation?.maxNumber !== null && validation?.maxNumber !== undefined && parsed > validation.maxNumber) {
      return t('schema.validation.maxNumber', { count: validation.maxNumber });
    }
  }

  if (validation?.pattern) {
    const pattern = new RegExp(validation.pattern);
    if (!pattern.test(textValue)) {
      return validation.patternMessage ?? t('schema.validation.invalidFormat');
    }
  }

  return null;
}

function validateFormRules(screen: UiScreen, value: SchemaRecord): string | null {
  for (const rule of screen.validationRules ?? []) {
    if (rule.type === 'atLeastOne') {
      const hasAnyValue = rule.fields.some((field) => normalizeText(value[field]).length > 0);
      if (!hasAnyValue) {
        return rule.message;
      }
    }
  }
  return null;
}

function humanizeFieldKey(key: string): string {
  return key
    .replace(/([a-z0-9])([A-Z])/g, '$1 $2')
    .replace(/^./, (value) => value.toUpperCase());
}

function normalizeText(value: SchemaRecordValue): string {
  if (value === null || value === undefined) {
    return '';
  }
  return String(value).trim();
}
