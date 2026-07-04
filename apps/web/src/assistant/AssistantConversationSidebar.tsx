// Copyright (c) Khaled Shawki. All rights reserved.

import BlueButton from '../components/BlueButton';
import { DirectionalText } from '../i18n/components/DirectionalText';
import { formatDateTime } from '../i18n/formatters';
import { useLocale } from '../i18n/LocaleProvider';
import type { AssistantConversationSummary } from './assistantTypes';

interface Props {
  activeConversationId: number | null;
  conversations: AssistantConversationSummary[];
  isLoading?: boolean;
  onNewConversation: () => void;
  onSelectConversation: (conversationId: number) => void;
  onArchiveConversation: (conversationId: number) => void;
}

export default function AssistantConversationSidebar({
  activeConversationId,
  conversations,
  isLoading = false,
  onNewConversation,
  onSelectConversation,
  onArchiveConversation,
}: Props) {
  const { locale, t } = useLocale();

  return (
    <aside className="assistant-conversation-sidebar" aria-label={t('assistant.conversations.aria')}>
      <div className="assistant-conversation-sidebar__header">
        <div>
          <p className="eyebrow">{t('navigation.assistant')}</p>
          <h2>{t('assistant.conversations.history')}</h2>
        </div>
        <BlueButton type="button" onClick={onNewConversation}>{t('assistant.conversations.newShort')}</BlueButton>
      </div>

      <button
        type="button"
        className={`assistant-conversation-item assistant-conversation-item--new ${activeConversationId === null ? 'assistant-conversation-item--active' : ''}`.trim()}
        onClick={onNewConversation}
      >
        <span>{t('assistant.conversations.new')}</span>
        <small>{t('assistant.conversations.newDescription')}</small>
      </button>

      <div className="assistant-conversation-list" aria-busy={isLoading}>
        {isLoading && <p className="assistant-conversation-empty">{t('assistant.conversations.loading')}</p>}

        {!isLoading && conversations.length === 0 && (
          <p className="assistant-conversation-empty">{t('assistant.conversations.empty')}</p>
        )}

        {!isLoading && conversations.map((conversation) => (
          <div
            key={conversation.id}
            className={`assistant-conversation-row ${conversation.id === activeConversationId ? 'assistant-conversation-row--active' : ''}`.trim()}
          >
            <button
              type="button"
              className="assistant-conversation-item"
              onClick={() => onSelectConversation(conversation.id)}
            >
              <span><DirectionalText value={conversation.title} /></span>
              <small>{formatDateTime(conversation.updatedAt, locale)}</small>
            </button>
            <button
              type="button"
              className="assistant-conversation-archive"
              aria-label={t('assistant.conversations.archiveAria', { title: conversation.title })}
              title={t('assistant.conversations.archive')}
              onClick={() => onArchiveConversation(conversation.id)}
            >
              ×
            </button>
          </div>
        ))}
      </div>
    </aside>
  );
}
