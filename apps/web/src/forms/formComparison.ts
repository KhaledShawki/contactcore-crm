// Copyright (c) Khaled Shawki. All rights reserved.

export type ComparableFormValue = string | number | boolean | null | ComparableFormValue[] | { [key: string]: ComparableFormValue | undefined } | undefined;

export function areFormValuesEqual(left: ComparableFormValue, right: ComparableFormValue): boolean {
  return stableSerialize(left) === stableSerialize(right);
}

export function isFormModified(baseline: ComparableFormValue, current: ComparableFormValue): boolean {
  return !areFormValuesEqual(baseline, current);
}

function stableSerialize(value: ComparableFormValue): string {
  return JSON.stringify(normalizeComparableValue(value));
}

function normalizeComparableValue(value: ComparableFormValue): unknown {
  if (value === undefined) {
    return null;
  }

  if (value === null || typeof value !== 'object') {
    return value;
  }

  if (Array.isArray(value)) {
    return value.map((item) => normalizeComparableValue(item));
  }

  return Object.keys(value)
    .sort()
    .reduce<Record<string, unknown>>((normalized, key) => {
      const nextValue = value[key];
      if (nextValue !== undefined) {
        normalized[key] = normalizeComparableValue(nextValue);
      }
      return normalized;
    }, {});
}
