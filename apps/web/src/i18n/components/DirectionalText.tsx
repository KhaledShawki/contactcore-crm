// Copyright (c) Khaled Shawki. All rights reserved.

export interface TextValueProps {
  value: string | number | null | undefined;
  className?: string;
}

export function DirectionalText({ value, className }: TextValueProps) {
  if (value === null || value === undefined || value === '') return null;
  return <bdi className={className} dir="auto">{value}</bdi>;
}
