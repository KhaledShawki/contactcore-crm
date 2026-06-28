// Copyright (c) Khaled Shawki. All rights reserved.

import { createSlice, type PayloadAction } from '@reduxjs/toolkit';
import { loadTheme, saveTheme } from './themeStorage';
import type { BlueDensity, BlueSidebarMode, BlueTextSize, BlueThemeMode } from './themeTypes';
import { defaultDensity, defaultSidebarMode, defaultTextSize } from './themeTypes';
import type { UiSettings } from '../schema/types';

interface ThemeState {
  mode: BlueThemeMode;
  textSize: BlueTextSize;
  density: BlueDensity;
  sidebarMode: BlueSidebarMode;
  reduceMotion: boolean;
  highContrast: boolean;
}

const initialState: ThemeState = {
  mode: loadTheme(),
  textSize: defaultTextSize,
  density: defaultDensity,
  sidebarMode: defaultSidebarMode,
  reduceMotion: false,
  highContrast: false,
};

const themeSlice = createSlice({
  name: 'theme',
  initialState,
  reducers: {
    setThemeMode(state, action: PayloadAction<BlueThemeMode>) {
      state.mode = action.payload;
      saveTheme(action.payload);
    },
    applyUiSettings(state, action: PayloadAction<UiSettings>) {
      state.mode = action.payload.theme;
      state.textSize = action.payload.textSize;
      state.density = action.payload.density;
      state.sidebarMode = action.payload.sidebarMode;
      state.reduceMotion = action.payload.reduceMotion;
      state.highContrast = action.payload.highContrast;
      saveTheme(action.payload.theme);
    },
  },
});

export const { setThemeMode, applyUiSettings } = themeSlice.actions;
export default themeSlice.reducer;
