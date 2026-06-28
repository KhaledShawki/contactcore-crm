// Copyright (c) Khaled Shawki. All rights reserved.

export type BlueThemeMode = 'light' | 'dark' | 'ocean' | 'graphite';
export type BlueTextSize = 'compact' | 'comfortable' | 'large';
export type BlueDensity = 'compact' | 'comfortable' | 'spacious';
export type BlueSidebarMode = 'expanded' | 'compact';

export interface BlueThemeOption {
  value: BlueThemeMode;
  label: string;
  description: string;
}

export interface UiPreferenceOption<TValue extends string> {
  value: TValue;
  label: string;
  description: string;
}

export const blueThemeOptions: BlueThemeOption[] = [
  { value: 'light', label: 'Light', description: 'Clean CRM workspace for daily use.' },
  { value: 'dark', label: 'Dark', description: 'Low-light workspace with strong contrast.' },
  { value: 'ocean', label: 'Ocean', description: 'Blue navigation and soft surfaces for a focused CRM workspace.' },
  { value: 'graphite', label: 'Graphite', description: 'Neutral professional theme for presentations.' },
];

export const textSizeOptions: UiPreferenceOption<BlueTextSize>[] = [
  { value: 'compact', label: 'Compact', description: 'More rows and smaller text for dense CRM work.' },
  { value: 'comfortable', label: 'Comfortable', description: 'Balanced default text size.' },
  { value: 'large', label: 'Large', description: 'Larger labels and values for readability.' },
];

export const densityOptions: UiPreferenceOption<BlueDensity>[] = [
  { value: 'compact', label: 'Compact', description: 'Reduced spacing for power users.' },
  { value: 'comfortable', label: 'Comfortable', description: 'Balanced spacing for daily work.' },
  { value: 'spacious', label: 'Spacious', description: 'More whitespace for presentation-style usage.' },
];

export const sidebarOptions: UiPreferenceOption<BlueSidebarMode>[] = [
  { value: 'expanded', label: 'Expanded', description: 'Full navigation labels.' },
  { value: 'compact', label: 'Compact', description: 'Narrower desktop navigation rail.' },
];

export const defaultBlueTheme: BlueThemeMode = 'ocean';
export const defaultTextSize: BlueTextSize = 'comfortable';
export const defaultDensity: BlueDensity = 'comfortable';
export const defaultSidebarMode: BlueSidebarMode = 'expanded';

export function isBlueThemeMode(value: unknown): value is BlueThemeMode {
  return typeof value === 'string' && blueThemeOptions.some((option) => option.value === value);
}
