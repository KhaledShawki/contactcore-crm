// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import {
  blueThemeOptions,
  defaultBlueTheme,
  densityOptions,
  isBlueThemeMode,
  sidebarOptions,
  textSizeOptions,
} from './themeTypes';

describe('themeTypes', () => {
  it('keeps a valid default theme and unique theme values', () => {
    const values = blueThemeOptions.map((option) => option.value);

    expect(isBlueThemeMode(defaultBlueTheme)).toBe(true);
    expect(new Set(values).size).toBe(values.length);
  });

  it('rejects unknown theme values', () => {
    expect(isBlueThemeMode('ocean')).toBe(true);
    expect(isBlueThemeMode('random')).toBe(false);
    expect(isBlueThemeMode(null)).toBe(false);
  });

  it('keeps UI preference option values stable for backend persistence', () => {
    expect(textSizeOptions.map((option) => option.value)).toEqual(['compact', 'comfortable', 'large']);
    expect(densityOptions.map((option) => option.value)).toEqual(['compact', 'comfortable', 'spacious']);
    expect(sidebarOptions.map((option) => option.value)).toEqual(['expanded', 'compact']);
  });
});
