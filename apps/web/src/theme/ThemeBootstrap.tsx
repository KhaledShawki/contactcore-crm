// Copyright (c) Khaled Shawki. All rights reserved.

import { useEffect, type PropsWithChildren } from 'react';
import { useAppSelector } from '../store/hooks';

export default function ThemeBootstrap({ children }: PropsWithChildren) {
  const theme = useAppSelector((state) => state.theme);

  useEffect(() => {
    document.documentElement.dataset.theme = theme.mode;
    document.documentElement.dataset.textSize = theme.textSize;
    document.documentElement.dataset.density = theme.density;
    document.documentElement.dataset.sidebar = theme.sidebarMode;
    document.documentElement.dataset.motion = theme.reduceMotion ? 'reduced' : 'normal';
    document.documentElement.dataset.contrast = theme.highContrast ? 'high' : 'normal';
    document.documentElement.style.colorScheme = theme.mode === 'light' || theme.mode === 'ocean' ? 'light' : 'dark';
  }, [theme]);

  return children;
}
