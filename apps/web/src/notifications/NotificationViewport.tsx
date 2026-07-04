// Copyright (c) Khaled Shawki. All rights reserved.

import { useLocale } from '../i18n/LocaleProvider';
import type { AppNotification } from './notificationTypes';

interface Props {
  notifications: AppNotification[];
  onDismiss: (id: string) => void;
}

export default function NotificationViewport({ notifications, onDismiss }: Props) {
  const { t } = useLocale();
  if (notifications.length === 0) {
    return null;
  }

  return (
    <output className="notification-viewport" aria-live="polite" aria-label={t('notifications.aria')}>
      <ul className="notification-list">
        {notifications.map((notification) => (
          <li key={notification.id} className={`notification-toast notification-toast--${notification.tone}`.trim()}>
            <div className="notification-toast__content">
              <span className="notification-toast__tone">{t(`notifications.tone.${notification.tone}`)}</span>
              <p>{notification.message}</p>
            </div>
            <button type="button" className="notification-toast__dismiss" aria-label={t('notifications.dismiss')} onClick={() => onDismiss(notification.id)}>
              ×
            </button>
          </li>
        ))}
      </ul>
    </output>
  );
}
