// Copyright (c) Khaled Shawki. All rights reserved.

import type { ReactElement } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useEffect } from 'react';
import { useMeQuery } from './authApi';
import { logout, setCurrentUser } from './authSlice';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import LoadingState from '../components/LoadingState';

interface Props {
  children: ReactElement;
}

export interface LoginRedirectState {
  from?: string;
}

export default function RequireAuth({ children }: Props) {
  const accessToken = useAppSelector((state) => state.auth.accessToken);
  const dispatch = useAppDispatch();
  const location = useLocation();
  const { data, isLoading, isError } = useMeQuery(undefined, { skip: !accessToken });

  useEffect(() => {
    if (data) {
      dispatch(setCurrentUser(data));
    }
  }, [data, dispatch]);

  useEffect(() => {
    if (isError) {
      dispatch(logout());
    }
  }, [dispatch, isError]);

  if (!accessToken) {
    const nextPath = `${location.pathname}${location.search}${location.hash}`;
    const redirectState: LoginRedirectState = { from: nextPath };
    return <Navigate to="/login" replace state={redirectState} />;
  }

  if (isLoading) {
    return <LoadingState />;
  }

  return children;
}
