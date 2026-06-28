// Copyright (c) Khaled Shawki. All rights reserved.

import type { SchemaRecord, SchemaRecordValue } from './schemaValues';
import type { UiField, UiScreen } from './types';

export interface SchemaValidationResult {
  valid: boolean;
  fieldErrors: Record<string, string>;
  formError: string | null;
}

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const urlPattern = /^https?:\/\/.+/i;

export function validateSchemaRecord(screen: UiScreen, value: SchemaRecord): SchemaValidationResult {
  const fieldErrors: Record<string, string> = {};

  for (const field of screen.fields) {
    if (!field.formVisible || field.readOnly) {
      continue;
    }
    const error = validateField(field, value[field.key]);
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

function validateField(field: UiField, value: SchemaRecordValue): string | null {
  if (field.type === 'checkbox') {
    return null;
  }

  const textValue = normalizeText(value);
  const validation = field.validation;
  const isEmpty = textValue.length === 0;

  if (field.required && isEmpty) {
    return `${field.label} is required.`;
  }
  if (isEmpty) {
    return null;
  }

  if (validation?.minLength !== null && validation?.minLength !== undefined && textValue.length < validation.minLength) {
    return `Minimum length is ${validation.minLength} characters.`;
  }
  if (validation?.maxLength !== null && validation?.maxLength !== undefined && textValue.length > validation.maxLength) {
    return `Maximum length is ${validation.maxLength} characters.`;
  }

  const inputType = validation?.inputType ?? field.type;
  if (inputType === 'email' && !emailPattern.test(textValue)) {
    return 'Enter a valid email address.';
  }
  if (inputType === 'url' && !urlPattern.test(textValue)) {
    return validation?.patternMessage ?? 'Enter a valid URL.';
  }
  if (inputType === 'number' || field.type === 'number') {
    const parsed = Number(textValue);
    if (!Number.isFinite(parsed)) {
      return 'Enter a valid number.';
    }
    if (validation?.minNumber !== null && validation?.minNumber !== undefined && parsed < validation.minNumber) {
      return `Minimum value is ${validation.minNumber}.`;
    }
    if (validation?.maxNumber !== null && validation?.maxNumber !== undefined && parsed > validation.maxNumber) {
      return `Maximum value is ${validation.maxNumber}.`;
    }
  }

  if (validation?.pattern) {
    const pattern = new RegExp(validation.pattern);
    if (!pattern.test(textValue)) {
      return validation.patternMessage ?? 'Invalid format.';
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

function normalizeText(value: SchemaRecordValue): string {
  if (value === null || value === undefined) {
    return '';
  }
  return String(value).trim();
}
