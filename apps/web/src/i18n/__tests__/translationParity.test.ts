// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { dictionaries } from '../locales';

 describe('translation dictionaries', () => {
  it('keeps German and Arabic keys aligned with English', () => {
    const englishKeys = Object.keys(dictionaries.en).sort();
    expect(Object.keys(dictionaries.de).sort()).toEqual(englishKeys);
    expect(Object.keys(dictionaries.ar).sort()).toEqual(englishKeys);
  });
});
