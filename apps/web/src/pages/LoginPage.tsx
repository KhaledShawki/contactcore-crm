// Copyright (c) Khaled Shawki. All rights reserved.

import { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useLoginMutation } from '../auth/authApi';
import { setCredentials } from '../auth/authSlice';
import type { LoginRedirectState } from '../auth/RequireAuth';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import BlueAlert from '../components/BlueAlert';
import BlueButton from '../components/BlueButton';
import BlueCard from '../components/BlueCard';
import BlueInput from '../components/BlueInput';

const defaultPostLoginPath = '/dashboard';
const allowedPostLoginPrefixes = ['/dashboard', '/customers', '/leads', '/suppliers', '/marketing-sources', '/reports', '/assistant', '/settings', '/profile'];

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const accessToken = useAppSelector((state) => state.auth.accessToken);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const [login, { isLoading }] = useLoginMutation();
  const postLoginPath = resolveSafePostLoginPath(location.state);

  if (accessToken) {
    return <Navigate to={postLoginPath} replace />;
  }

  async function submit() {
    try {
      const response = await login({ username, password }).unwrap();
      dispatch(setCredentials(response));
      navigate(postLoginPath, { replace: true });
    } catch {
      setError('Login failed. Check the username and password.');
    }
  }

  return (
    <main className="login-page">
      <BlueCard eyebrow="ContactCore CRM" title="Sign in">
        {error && <BlueAlert message={error} />}
        <form className="login-form" onSubmit={(event) => { event.preventDefault(); void submit(); }}>
          <BlueInput label="Username" value={username} autoComplete="username" onChange={(event) => setUsername(event.target.value)} />
          <BlueInput label="Password" type="password" value={password} autoComplete="current-password" onChange={(event) => setPassword(event.target.value)} />
          <BlueButton type="submit" disabled={isLoading}>{isLoading ? 'Signing in...' : 'Sign in'}</BlueButton>
        </form>
      </BlueCard>
    </main>
  );
}

function resolveSafePostLoginPath(state: unknown): string {
  const candidate = (state as LoginRedirectState | null | undefined)?.from;
  if (typeof candidate !== 'string') {
    return defaultPostLoginPath;
  }

  if (!candidate.startsWith('/') || candidate.startsWith('//') || candidate.includes('://') || candidate.includes('\\')) {
    return defaultPostLoginPath;
  }

  return allowedPostLoginPrefixes.some((prefix) => candidate === prefix || candidate.startsWith(`${prefix}/`))
    ? candidate
    : defaultPostLoginPath;
}
