// Copyright (c) Khaled Shawki. All rights reserved.

import { useCallback, useRef, useState } from 'react';

export function useSingleFlightAction<TArgs extends unknown[]>(action: (...args: TArgs) => Promise<void>) {
  const runningRef = useRef(false);
  const [running, setRunning] = useState(false);

  const run = useCallback(async (...args: TArgs): Promise<boolean> => {
    if (runningRef.current) {
      return false;
    }

    runningRef.current = true;
    setRunning(true);

    try {
      await action(...args);
      return true;
    } finally {
      runningRef.current = false;
      setRunning(false);
    }
  }, [action]);

  return { run, running };
}

export function useSingleFlightByKey<TKey extends string | number>() {
  const runningKeysRef = useRef<Set<string> | null>(null);
  const [runningKeys, setRunningKeys] = useState<ReadonlySet<string>>(() => new Set());

  const run = useCallback(async (key: TKey, action: () => Promise<void>): Promise<boolean> => {
    const normalizedKey = String(key);
    const currentKeys = runningKeysRef.current ?? new Set<string>();
    if (currentKeys.has(normalizedKey)) {
      return false;
    }

    runningKeysRef.current = new Set(currentKeys).add(normalizedKey);
    setRunningKeys(new Set(runningKeysRef.current));

    try {
      await action();
      return true;
    } finally {
      const nextKeys = new Set(runningKeysRef.current ?? []);
      nextKeys.delete(normalizedKey);
      runningKeysRef.current = nextKeys;
      setRunningKeys(new Set(nextKeys));
    }
  }, []);

  const isRunning = useCallback((key: TKey | undefined | null): boolean => {
    if (key === undefined || key === null) {
      return false;
    }
    return runningKeys.has(String(key));
  }, [runningKeys]);

  return { run, isRunning };
}
