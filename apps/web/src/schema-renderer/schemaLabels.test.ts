// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { translatedLabel, translateDataLabel } from './schemaLabels';

const t = (key: string) => key === 'known.key' ? 'Known label' : key;

describe('schemaLabels', () => {
  it('uses translated labels with fallback text', () => {
    expect(translatedLabel('known.key', 'Fallback', t)).toBe('Known label');
    expect(translatedLabel('missing.key', 'Fallback', t)).toBe('Fallback');
  });

  it('translates normalized data labels when a translation exists', () => {
    const translate = (key: string) => key === 'data.label.ACTIVE_CUSTOMER' ? 'Active customer' : key;
    expect(translateDataLabel('Active Customer', translate)).toBe('Active customer');
    expect(translateDataLabel('Unknown', translate)).toBe('Unknown');
  });
});
