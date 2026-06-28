// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { validateSchemaRecord } from './schemaValidation';
import type { UiScreen } from './types';

const customerScreen: UiScreen = {
  key: 'customers',
  title: 'Customers',
  entityKind: 'CUSTOMER',
  listEndpoint: '',
  detailEndpoint: '',
  createEndpoint: '',
  updateEndpoint: '',
  archiveEndpoint: '',
  documentEndpoint: '',
  fields: [
    { key: 'code', label: 'Code', type: 'text', required: true, listVisible: true, formVisible: true, readOnly: false, defaultValue: null, options: [], validation: { minLength: 2, maxLength: 64, pattern: '^[A-Z0-9][A-Z0-9_-]{1,63}$', patternMessage: 'Use uppercase letters, numbers, underscore, or dash.' } },
    { key: 'name', label: 'Name', type: 'text', required: true, listVisible: true, formVisible: true, readOnly: false, defaultValue: null, options: [], validation: { minLength: 2, maxLength: 255 } },
    { key: 'primaryEmail', label: 'Email', type: 'text', required: false, listVisible: true, formVisible: true, readOnly: false, defaultValue: null, options: [], validation: { inputType: 'email', maxLength: 255 } },
    { key: 'notes', label: 'Notes', type: 'textarea', required: false, listVisible: false, formVisible: true, readOnly: false, defaultValue: null, options: [], validation: { maxLength: 10 } },
  ],
  validationRules: [],
};

const contactScreen: UiScreen = {
  ...customerScreen,
  key: 'contactPersons',
  fields: [
    { key: 'firstName', label: 'First name', type: 'text', required: true, listVisible: true, formVisible: true, readOnly: false, defaultValue: null, options: [], validation: { maxLength: 120 } },
    { key: 'lastName', label: 'Last name', type: 'text', required: true, listVisible: true, formVisible: true, readOnly: false, defaultValue: null, options: [], validation: { maxLength: 120 } },
    { key: 'email', label: 'Email', type: 'text', required: false, listVisible: true, formVisible: true, readOnly: false, defaultValue: null, options: [], validation: { inputType: 'email', maxLength: 255 } },
    { key: 'phone', label: 'Phone', type: 'text', required: false, listVisible: true, formVisible: true, readOnly: false, defaultValue: null, options: [], validation: { pattern: '^[+()0-9\\s./-]{3,64}$', patternMessage: 'Use digits, spaces, +, -, /, ., or parentheses only.' } },
    { key: 'mobile', label: 'Mobile', type: 'text', required: false, listVisible: true, formVisible: true, readOnly: false, defaultValue: null, options: [], validation: { pattern: '^[+()0-9\\s./-]{3,64}$' } },
  ],
  validationRules: [{ type: 'atLeastOne', fields: ['email', 'phone', 'mobile'], message: 'Add at least one email, phone, or mobile number.' }],
};

describe('schemaValidation', () => {
  it('validates required fields, patterns, email type, and max length from schema metadata', () => {
    const result = validateSchemaRecord(customerScreen, {
      code: 'bad code',
      name: '',
      primaryEmail: 'broken-email',
      notes: 'this is too long',
    });

    expect(result.valid).toBe(false);
    expect(result.fieldErrors.code).toContain('uppercase');
    expect(result.fieldErrors.name).toBe('Name is required.');
    expect(result.fieldErrors.primaryEmail).toBe('Enter a valid email address.');
    expect(result.fieldErrors.notes).toBe('Maximum length is 10 characters.');
  });

  it('validates cross-field form rules from schema metadata', () => {
    const result = validateSchemaRecord(contactScreen, { firstName: 'Sara', lastName: 'Fischer', email: '', phone: '', mobile: '' });

    expect(result.valid).toBe(false);
    expect(result.formError).toBe('Add at least one email, phone, or mobile number.');
  });

  it('accepts a valid schema-driven contact record', () => {
    const result = validateSchemaRecord(contactScreen, { firstName: 'Sara', lastName: 'Fischer', email: 'sara@example.test' });

    expect(result.valid).toBe(true);
    expect(result.fieldErrors).toEqual({});
    expect(result.formError).toBeNull();
  });
});
