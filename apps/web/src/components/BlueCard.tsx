// Copyright (c) Khaled Shawki. All rights reserved.

import type { PropsWithChildren, ReactNode } from 'react';

interface Props {
  eyebrow?: string;
  title?: string;
  action?: ReactNode;
}

export default function BlueCard({ eyebrow, title, action, children }: PropsWithChildren<Props>) {
  return (
    <section className="blue-card">
      {(title || action) && (
        <header className="blue-card__header">
          <div>
            {eyebrow && <p className="eyebrow">{eyebrow}</p>}
            {title && <h1>{title}</h1>}
          </div>
          {action}
        </header>
      )}
      {children}
    </section>
  );
}
