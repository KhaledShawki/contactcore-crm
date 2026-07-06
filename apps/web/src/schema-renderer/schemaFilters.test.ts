// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { commercialDashboardQueryArgs, initialFilterValues, updateFilterValue } from './schemaFilters';
import type { UiScreenFilter } from '../schema/types';

const filters: UiScreenFilter[] = [
  { key: 'from', type: 'date', label: 'From' },
  { key: 'to', type: 'date', label: 'To' },
  { key: 'currency', type: 'select', label: 'Currency', defaultValue: '', options: ['', 'CHF'] },
  { key: 'limit', type: 'number', label: 'Top results', defaultValue: '10', min: 1, max: 50 },
  { key: 'groupBy', type: 'select', label: 'Group by', defaultValue: 'MONTH', options: ['MONTH'] },
];

describe('schemaFilters', () => {
  it('builds initial values from backend filter metadata', () => {
    expect(initialFilterValues(filters)).toEqual({
      from: '',
      to: '',
      currency: '',
      limit: '10',
      groupBy: 'MONTH',
    });
  });

  it('updates one filter without mutating the current values', () => {
    const current = initialFilterValues(filters);

    expect(updateFilterValue(current, 'currency', 'CHF')).toEqual({ ...current, currency: 'CHF' });
    expect(current.currency).toBe('');
  });

  it('maps filter values to commercial dashboard query arguments', () => {
    expect(commercialDashboardQueryArgs({
      from: '2026-01-01',
      to: '2026-12-31',
      currency: 'CHF',
      limit: '25',
      groupBy: 'MONTH',
    })).toEqual({
      from: '2026-01-01',
      to: '2026-12-31',
      currency: 'CHF',
      limit: 25,
      groupBy: 'MONTH',
    });
  });

  it('omits blank and invalid optional filter values', () => {
    expect(commercialDashboardQueryArgs({ currency: '', limit: 'not-a-number' })).toBeUndefined();
  });
});
