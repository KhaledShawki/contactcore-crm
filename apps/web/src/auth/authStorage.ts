// Copyright (c) Khaled Shawki. All rights reserved.

import { directionFor, normalizeLocale } from '../i18n/localeRegistry';
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
    return { accessToken, user: normalizePersistedUser(JSON.parse(rawUser) as Partial<CurrentUser>) };
  } catch {
    clearAuthState();
    return { accessToken: null, user: null };
  }
}

export function saveAuthState(accessToken: string, user: CurrentUser): void {
  localStorage.setItem(TOKEN_KEY, accessToken);
  localStorage.setItem(USER_KEY, JSON.stringify(normalizePersistedUser(user)));
}

export function clearAuthState(): void {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

function normalizePersistedUser(user: Partial<CurrentUser>): CurrentUser {
  const locale = normalizeLocale(user.locale);
  return {
    id: Number(user.id),
    username: String(user.username ?? ''),
    email: String(user.email ?? ''),
    displayName: String(user.displayName ?? user.username ?? ''),
    locale,
    direction: directionFor(locale),
    roles: Array.isArray(user.roles) ? user.roles : [],
  };
}
