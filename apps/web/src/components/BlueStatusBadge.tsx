// Copyright (c) Khaled Shawki. All rights reserved.

interface Props {
  value: string;
}

export default function BlueStatusBadge({ value }: Props) {
  const tone = resolveTone(value);
  return <span className={`blue-status blue-status--${tone}`}>{value}</span>;
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
