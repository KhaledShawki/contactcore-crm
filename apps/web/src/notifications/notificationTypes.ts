// Copyright (c) Khaled Shawki. All rights reserved.

export type NotificationTone = 'success' | 'error' | 'info' | 'warning';

export interface AppNotification {
  id: string;
  tone: NotificationTone;
  message: string;
  createdAt: number;
  dismissAfterMs: number;
}

export interface NotifyOptions {
  dismissAfterMs?: number;
}

export interface NotificationActions {
  notify: (tone: NotificationTone, message: string, options?: NotifyOptions) => string;
  notifySuccess: (message: string, options?: NotifyOptions) => string;
  notifyError: (message: string, options?: NotifyOptions) => string;
  notifyInfo: (message: string, options?: NotifyOptions) => string;
  notifyWarning: (message: string, options?: NotifyOptions) => string;
  dismissNotification: (id: string) => void;
}
