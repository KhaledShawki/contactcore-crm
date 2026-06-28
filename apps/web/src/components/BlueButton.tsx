// Copyright (c) Khaled Shawki. All rights reserved.

import type { ButtonHTMLAttributes, PropsWithChildren } from 'react';

type Variant = 'primary' | 'secondary' | 'danger';

interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
}

export default function BlueButton({ children, className = '', type = 'button', variant = 'primary', ...props }: PropsWithChildren<Props>) {
  return (
    <button type={type} className={`blue-button blue-button--${variant} ${className}`.trim()} {...props}>
      {children}
    </button>
  );
}
