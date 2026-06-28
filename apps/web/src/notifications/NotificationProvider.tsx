// Copyright (c) Khaled Shawki. All rights reserved.

import { createContext, useCallback, useEffect, useMemo, useRef, useState, type PropsWithChildren } from 'react';
import NotificationViewport from './NotificationViewport';
import type { AppNotification, NotificationActions, NotificationTone, NotifyOptions } from './notificationTypes';

const defaultDismissAfterMs = 4_000;
const maxVisibleNotifications = 4;
const NotificationContext = createContext<NotificationActions | null>(null);

export function NotificationProvider({ children }: PropsWithChildren) {
  const [notifications, setNotifications] = useState<AppNotification[]>([]);
  const sequence = useRef(0);

  const dismissNotification = useCallback((id: string) => {
    setNotifications((current) => current.filter((notification) => notification.id !== id));
  }, []);

  const notify = useCallback((tone: NotificationTone, message: string, options: NotifyOptions = {}) => {
    const trimmedMessage = message.trim();
    if (!trimmedMessage) {
      return '';
    }

    const id = `notification-${Date.now()}-${sequence.current++}`;
    const nextNotification: AppNotification = {
      id,
      tone,
      message: trimmedMessage,
      createdAt: Date.now(),
      dismissAfterMs: options.dismissAfterMs ?? defaultDismissAfterMs,
    };

    setNotifications((current) => [...current, nextNotification].slice(-maxVisibleNotifications));
    return id;
  }, []);

  const value = useMemo<NotificationActions>(() => ({
    notify,
    notifySuccess: (message, options) => notify('success', message, options),
    notifyError: (message, options) => notify('error', message, options),
    notifyInfo: (message, options) => notify('info', message, options),
    notifyWarning: (message, options) => notify('warning', message, options),
    dismissNotification,
  }), [dismissNotification, notify]);

  return (
    <NotificationContext.Provider value={value}>
      {children}
      <NotificationAutoDismiss notifications={notifications} onDismiss={dismissNotification} />
      <NotificationViewport notifications={notifications} onDismiss={dismissNotification} />
    </NotificationContext.Provider>
  );
}

export { NotificationContext };

function NotificationAutoDismiss({ notifications, onDismiss }: { notifications: AppNotification[]; onDismiss: (id: string) => void }) {
  useEffect(() => {
    if (notifications.length === 0) {
      return undefined;
    }

    const timers: number[] = [];
    for (const notification of notifications) {
      if (notification.dismissAfterMs <= 0) {
        continue;
      }
      timers.push(window.setTimeout(() => onDismiss(notification.id), notification.dismissAfterMs));
    }

    return () => {
      for (const timer of timers) {
        window.clearTimeout(timer);
      }
    };
  }, [notifications, onDismiss]);

  return null;
}
