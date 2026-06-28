// Copyright (c) Khaled Shawki. All rights reserved.

import { Link } from 'react-router-dom';
import type { AssistantReference } from './assistantTypes';

interface Props {
  references: AssistantReference[];
}

export default function AssistantReferenceList({ references }: Props) {
  if (references.length === 0) {
    return null;
  }

  return (
    <div className="assistant-references" aria-label="Assistant record references">
      {references.map((reference) => (
        <Link
          key={`${reference.entityType}-${reference.entityId}-${reference.route}`}
          className="assistant-reference-card"
          to={reference.route}
        >
          <span>{reference.entityType.replaceAll('_', ' ')}</span>
          <strong>{reference.label}</strong>
        </Link>
      ))}
    </div>
  );
}
