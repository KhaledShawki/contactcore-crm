// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { areFormValuesEqual, isFormModified } from './formComparison';

describe('formComparison', () => {
  it('compares objects independent of key insertion order', () => {
    expect(areFormValuesEqual({ name: 'Meyer', code: 'CUS-1' }, { code: 'CUS-1', name: 'Meyer' })).toBe(true);
  });

  it('detects a changed writable payload', () => {
    expect(isFormModified({ name: 'Meyer', active: true }, { name: 'Meyer AG', active: true })).toBe(true);
  });

  it('ignores undefined fields that are not part of the submitted payload', () => {
    expect(areFormValuesEqual({ name: 'Meyer', unused: undefined }, { name: 'Meyer' })).toBe(true);
  });
});
