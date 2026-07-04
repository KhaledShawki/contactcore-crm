// Copyright (c) Khaled Shawki. All rights reserved.

import en from './en';
import de from './de';
import ar from './ar';
import type { SupportedLocale } from '../localeRegistry';

export const dictionaries: Record<SupportedLocale, Record<string, string>> = { en, de, ar };

export type TranslationKey = keyof typeof en;
