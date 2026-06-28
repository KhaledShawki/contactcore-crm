// Copyright (c) Khaled Shawki. All rights reserved.

import { use } from 'react';
import { NotificationContext } from './NotificationProvider';

export function useNotifications() {
  const context = use(NotificationContext);
  if (!context) {
    throw new Error('useNotifications must be used inside NotificationProvider.');
  }
  return context;
}
