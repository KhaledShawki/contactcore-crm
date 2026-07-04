// Copyright (c) Khaled Shawki. All rights reserved.

import { Link } from 'react-router-dom';
import { DirectionalText } from '../i18n/components/DirectionalText';
import { useLocale } from '../i18n/LocaleProvider';
import type { AssistantReference } from './assistantTypes';

interface Props {
  references: AssistantReference[];
}

export default function AssistantReferenceList({ references }: Props) {
  const { t } = useLocale();
  if (references.length === 0) {
    return null;
  }

  return (
    <div className="assistant-references" aria-label={t('assistant.references.aria')}>
      {references.map((reference) => (
        <Link
          key={`${reference.entityType}-${reference.entityId}-${reference.route}`}
          className="assistant-reference-card"
          to={reference.route}
        >
          <span>{referenceTypeLabel(reference.entityType, t)}</span>
          <strong><DirectionalText value={reference.label} /></strong>
        </Link>
      ))}
    </div>
  );
}

function referenceTypeLabel(entityType: string, t: (key: string) => string): string {
  const key = `assistant.reference.type.${entityType}`;
  const translated = t(key);
  return translated === key ? entityType.replaceAll('_', ' ') : translated;
}
