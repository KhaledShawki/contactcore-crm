// Copyright (c) Khaled Shawki. All rights reserved.

import BlueAlert from '../components/BlueAlert';
import { DirectionalText } from '../i18n/components/DirectionalText';
import { useLocale } from '../i18n/LocaleProvider';
import AssistantReferenceList from './AssistantReferenceList';
import type { AssistantMessage } from './assistantTypes';

interface Props {
  message: AssistantMessage;
}

function assistantMeta(message: AssistantMessage, t: (key: string, params?: Record<string, string | number>) => string): string | null {
  if (!message.modelName && !message.answerSource && !message.answerStatus) {
    return null;
  }

  const parts = [
    message.modelName ? t('assistant.message.meta.model', { model: message.modelName }) : null,
    message.answerSource ? t('assistant.message.meta.source', { source: message.answerSource.toLowerCase() }) : null,
    message.answerStatus && message.answerStatus !== 'SUCCESS' ? t('assistant.message.meta.status', { status: message.answerStatus.toLowerCase() }) : null,
  ].filter(Boolean);

  return parts.join(' · ');
}

export default function AssistantMessageBubble({ message }: Props) {
  const { t } = useLocale();
  const isAssistant = message.role === 'ASSISTANT';
  const meta = isAssistant ? assistantMeta(message, t) : null;

  return (
    <article className={`assistant-message-bubble assistant-message-bubble--${message.role.toLowerCase()}`}>
      <header className="assistant-message-bubble__header">
        {isAssistant ? t('assistant.role.assistant') : t('assistant.role.you')}
      </header>
      <div className="assistant-message-bubble__content">
        {message.warning && <BlueAlert tone="warning" message={message.warning} />}
        <DirectionalText value={message.content} />
      </div>
      {isAssistant && <AssistantReferenceList references={message.references} />}
      {meta && <small className="assistant-message-bubble__meta">{meta}</small>}
    </article>
  );
}
