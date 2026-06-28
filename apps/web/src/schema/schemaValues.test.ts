// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { createDefaultRecord, toWritablePayload } from './schemaValues';
import type { UiScreen } from './types';

const screen: UiScreen = {
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
    { key: 'kind', label: 'Kind', type: 'hidden', required: true, listVisible: false, formVisible: false, readOnly: false, defaultValue: 'CUSTOMER', options: [] },
    { key: 'statusCode', label: 'Status', type: 'select', required: true, listVisible: true, formVisible: true, readOnly: false, defaultValue: 'ACTIVE', options: ['ACTIVE'] },
    { key: 'email', label: 'Email', type: 'text', required: false, listVisible: true, formVisible: true, readOnly: false, defaultValue: null, options: [] },
    { key: 'createdAt', label: 'Created', type: 'text', required: false, listVisible: false, formVisible: true, readOnly: true, defaultValue: null, options: [] },
  ],
};

describe('schemaValues', () => {
  it('builds default records from backend schema defaults', () => {
    expect(createDefaultRecord(screen)).toEqual({ kind: 'CUSTOMER', statusCode: 'ACTIVE' });
  });

  it('omits readonly fields and normalizes empty optional fields', () => {
    const payload = toWritablePayload(screen, { kind: 'CUSTOMER', statusCode: 'ACTIVE', email: ' ', createdAt: '2026-01-01' });

    expect(payload).toEqual({ kind: 'CUSTOMER', statusCode: 'ACTIVE' });
  });
});
