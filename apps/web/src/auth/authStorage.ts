// Copyright (c) Khaled Shawki. All rights reserved.

import type { CurrentUser } from './authTypes';

const TOKEN_KEY = 'contactcore.accessToken';
const USER_KEY = 'contactcore.currentUser';

export interface PersistedAuthState {
  accessToken: string | null;
  user: CurrentUser | null;
}

export function loadAuthState(): PersistedAuthState {
  const accessToken = localStorage.getItem(TOKEN_KEY);
  const rawUser = localStorage.getItem(USER_KEY);
  if (!accessToken || !rawUser) {
    return { accessToken: null, user: null };
  }

  try {
    return { accessToken, user: JSON.parse(rawUser) as CurrentUser };
  } catch {
    clearAuthState();
    return { accessToken: null, user: null };
  }
}

export function saveAuthState(accessToken: string, user: CurrentUser): void {
  localStorage.setItem(TOKEN_KEY, accessToken);
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function clearAuthState(): void {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}
