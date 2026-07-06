// Copyright (c) Khaled Shawki. All rights reserved.

import type { CommercialDashboardQueryArgs } from '../dashboard/commercialDashboardApi';
import type { UiScreenFilter } from '../schema/types';

export type SchemaFilterValues = Record<string, string>;

export function initialFilterValues(filters: UiScreenFilter[] | null | undefined): SchemaFilterValues {
  return Object.fromEntries((filters ?? []).map((filter) => [filter.key, filter.defaultValue ?? '']));
}

function normalizeFilterValue(value: string | number | null | undefined): string {
  return value == null ? '' : String(value);
}

export function updateFilterValue(values: SchemaFilterValues, key: string, value: string | number | null | undefined): SchemaFilterValues {
  return { ...values, [key]: normalizeFilterValue(value) };
}

export function commercialDashboardQueryArgs(values: SchemaFilterValues): CommercialDashboardQueryArgs | undefined {
  const args: CommercialDashboardQueryArgs = {};
  const from = optional(values.from);
  const to = optional(values.to);
  const currency = optional(values.currency);
  const groupBy = optional(values.groupBy);
  const limit = numberValue(values.limit);

  if (from) args.from = from;
  if (to) args.to = to;
  if (currency) args.currency = currency;
  if (limit != null) args.limit = limit;
  if (groupBy) args.groupBy = groupBy;

  return Object.keys(args).length === 0 ? undefined : args;
}

function optional(value: string | undefined): string | undefined {
  const normalized = value?.trim();
  return normalized ? normalized : undefined;
}

function numberValue(value: string | undefined): number | undefined {
  const normalized = optional(value);
  if (!normalized) return undefined;
  const parsed = Number(normalized);
  return Number.isFinite(parsed) ? parsed : undefined;
}
