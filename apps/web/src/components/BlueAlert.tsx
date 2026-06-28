// Copyright (c) Khaled Shawki. All rights reserved.

interface Props {
  message: string;
  tone?: 'error' | 'info' | 'success' | 'warning';
}

export default function BlueAlert({ message, tone = 'error' }: Props) {
  return <div className={`blue-alert blue-alert--${tone}`}>{message}</div>;
}
