// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { asArray, readNumber, readPath, readString } from './schemaData';

describe('schemaData', () => {
  it('reads nested values by dot path', () => {
    expect(readPath({ dashboard: { kpis: [1, 2] } }, 'dashboard.kpis')).toEqual([1, 2]);
  });

  it('normalizes arrays and primitive field values safely', () => {
    expect(asArray([{ id: 1 }])).toHaveLength(1);
    expect(asArray(null)).toEqual([]);
    expect(readString({ label: 'Customers' }, 'label')).toBe('Customers');
    expect(readNumber({ value: '42' }, 'value')).toBe(42);
    expect(readNumber({ value: 'n/a' }, 'value')).toBe(0);
  });
});
