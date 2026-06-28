// Copyright (c) Khaled Shawki. All rights reserved.

import BlueButton from '../components/BlueButton';
import type { AssistantConversationSummary } from './assistantTypes';

const conversationUpdatedAtFormatter = new Intl.DateTimeFormat(undefined, {
  month: 'short',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
});

interface Props {
  activeConversationId: number | null;
  conversations: AssistantConversationSummary[];
  isLoading?: boolean;
  onNewConversation: () => void;
  onSelectConversation: (conversationId: number) => void;
  onArchiveConversation: (conversationId: number) => void;
}

function formatUpdatedAt(value: string): string {
  return conversationUpdatedAtFormatter.format(new Date(value));
}

export default function AssistantConversationSidebar({
  activeConversationId,
  conversations,
  isLoading = false,
  onNewConversation,
  onSelectConversation,
  onArchiveConversation,
}: Props) {
  return (
    <aside className="assistant-conversation-sidebar" aria-label="Assistant conversations">
      <div className="assistant-conversation-sidebar__header">
        <div>
          <p className="eyebrow">Assistant</p>
          <h2>History</h2>
        </div>
        <BlueButton type="button" onClick={onNewConversation}>New</BlueButton>
      </div>

      <button
        type="button"
        className={`assistant-conversation-item assistant-conversation-item--new ${activeConversationId === null ? 'assistant-conversation-item--active' : ''}`.trim()}
        onClick={onNewConversation}
      >
        <span>New conversation</span>
        <small>Start a clean assistant thread</small>
      </button>

      <div className="assistant-conversation-list" aria-busy={isLoading}>
        {isLoading && <p className="assistant-conversation-empty">Loading conversations…</p>}

        {!isLoading && conversations.length === 0 && (
          <p className="assistant-conversation-empty">No previous assistant conversations yet.</p>
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
              <span>{conversation.title}</span>
              <small>{formatUpdatedAt(conversation.updatedAt)}</small>
            </button>
            <button
              type="button"
              className="assistant-conversation-archive"
              aria-label={`Archive conversation ${conversation.title}`}
              title="Archive conversation"
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
