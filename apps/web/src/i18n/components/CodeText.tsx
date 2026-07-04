// Copyright (c) Khaled Shawki. All rights reserved.

import type { TextValueProps } from './DirectionalText';

export function CodeText({ value, className }: TextValueProps) {
  if (value === null || value === undefined || value === '') return null;
  return <span className={className ? `code-value ${className}` : 'code-value'} dir="ltr">{value}</span>;
}
