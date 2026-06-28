// Copyright (c) Khaled Shawki. All rights reserved.

import type { AppNotification } from './notificationTypes';

interface Props {
  notifications: AppNotification[];
  onDismiss: (id: string) => void;
}

export default function NotificationViewport({ notifications, onDismiss }: Props) {
  if (notifications.length === 0) {
    return null;
  }

  return (
    <output className="notification-viewport" aria-live="polite" aria-label="Application notifications">
      <ul className="notification-list">
        {notifications.map((notification) => (
          <li key={notification.id} className={`notification-toast notification-toast--${notification.tone}`.trim()}>
            <div className="notification-toast__content">
              <span className="notification-toast__tone">{formatTone(notification.tone)}</span>
              <p>{notification.message}</p>
            </div>
            <button type="button" className="notification-toast__dismiss" aria-label="Dismiss notification" onClick={() => onDismiss(notification.id)}>
              ×
            </button>
          </li>
        ))}
      </ul>
    </output>
  );
}

function formatTone(tone: AppNotification['tone']) {
  switch (tone) {
    case 'success':
      return 'Success';
    case 'error':
      return 'Error';
    case 'warning':
      return 'Warning';
    case 'info':
      return 'Info';
  }
}
