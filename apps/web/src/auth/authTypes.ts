// Copyright (c) Khaled Shawki. All rights reserved.

export interface CurrentUser {
  id: number;
  username: string;
  email: string;
  displayName: string;
  roles: string[];
}

export interface AuthResponse {
  accessToken: string;
  tokenType: 'Bearer';
  user: CurrentUser;
}

export interface LoginRequest {
  username: string;
  password: string;
}
