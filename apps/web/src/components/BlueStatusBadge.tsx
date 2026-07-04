// Copyright (c) Khaled Shawki. All rights reserved.

import { useLocale } from '../i18n/LocaleProvider';

interface Props {
  value: string;
}

export default function BlueStatusBadge({ value }: Props) {
  const { t } = useLocale();
  const tone = resolveTone(value);
  return <span className={`blue-status blue-status--${tone}`}>{localizedStatus(value, t)}</span>;
}

function localizedStatus(value: string, t: (key: string) => string): string {
  const key = `data.label.${value.trim().toUpperCase().replace(/\s+/g, '_')}`;
  const translated = t(key);
  return translated === key ? value : translated;
}

function resolveTone(value: string): 'success' | 'warning' | 'neutral' {
  const normalized = value.toLowerCase();
  if (normalized.includes('active') || normalized.includes('customer') || normalized.includes('qualified')) {
    return 'success';
  }
  if (normalized.includes('lead') || normalized.includes('prospect') || normalized.includes('new')) {
    return 'warning';
  }
  return 'neutral';
}
