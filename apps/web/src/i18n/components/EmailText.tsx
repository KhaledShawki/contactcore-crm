// Copyright (c) Khaled Shawki. All rights reserved.

import type { TextValueProps } from './DirectionalText';

export function EmailText({ value, className }: TextValueProps) {
  if (value === null || value === undefined || value === '') return null;
  return <span className={className} dir="ltr">{value}</span>;
}
