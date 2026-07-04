// Copyright (c) Khaled Shawki. All rights reserved.

import { useLocale } from '../i18n/LocaleProvider';

export default function AssistantThinkingIndicator() {
  const { t } = useLocale();
  return (
    <article className="assistant-message-bubble assistant-message-bubble--assistant assistant-message-bubble--pending" aria-label={t('assistant.answering.aria')}>
      <header className="assistant-message-bubble__header">{t('assistant.role.assistant')}</header>
      <output className="assistant-thinking" aria-live="polite">
        <span className="assistant-thinking__dot" aria-hidden="true" />
        <span className="assistant-thinking__dot" aria-hidden="true" />
        <span className="assistant-thinking__dot" aria-hidden="true" />
        <span className="assistant-thinking__label">{t('assistant.answering.label')}</span>
      </output>
    </article>
  );
}
