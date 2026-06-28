// Copyright (c) Khaled Shawki. All rights reserved.

import { createSlice, type PayloadAction } from '@reduxjs/toolkit';
import type { AuthResponse, CurrentUser } from './authTypes';
import { clearAuthState, loadAuthState, saveAuthState } from './authStorage';

interface AuthState {
  accessToken: string | null;
  user: CurrentUser | null;
}

const initialState: AuthState = loadAuthState();

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials(state, action: PayloadAction<AuthResponse>) {
      state.accessToken = action.payload.accessToken;
      state.user = action.payload.user;
      saveAuthState(action.payload.accessToken, action.payload.user);
    },
    setCurrentUser(state, action: PayloadAction<CurrentUser>) {
      state.user = action.payload;
      if (state.accessToken) {
        saveAuthState(state.accessToken, action.payload);
      }
    },
    logout(state) {
      state.accessToken = null;
      state.user = null;
      clearAuthState();
    },
  },
});

export const { setCredentials, setCurrentUser, logout } = authSlice.actions;
export default authSlice.reducer;
