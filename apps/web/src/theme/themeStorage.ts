// Copyright (c) Khaled Shawki. All rights reserved.

import { defaultBlueTheme, isBlueThemeMode, type BlueThemeMode } from './themeTypes';

const THEME_KEY = 'contactcore.blueTheme';

export function loadTheme(): BlueThemeMode {
  const stored = localStorage.getItem(THEME_KEY);
  return isBlueThemeMode(stored) ? stored : defaultBlueTheme;
}

export function saveTheme(theme: BlueThemeMode): void {
  localStorage.setItem(THEME_KEY, theme);
}
