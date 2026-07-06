// Copyright (c) Khaled Shawki. All rights reserved.

import BlueInput from '../components/BlueInput';
import BlueSelect from '../components/BlueSelect';
import { useLocale } from '../i18n/LocaleProvider';
import type { UiScreenFilter } from '../schema/types';
import { translatedLabel } from './schemaLabels';
import type { SchemaFilterValues } from './schemaFilters';

interface Props {
  filters: UiScreenFilter[];
  values: SchemaFilterValues;
  onChange: (key: string, value: string) => void;
}

export default function SchemaFilterBar({ filters, values, onChange }: Props) {
  const { t } = useLocale();
  const visibleFilters = filters.filter((filter) => filter.key && filter.type);
  if (visibleFilters.length === 0) return null;

  return (
    <section className="schema-filter-bar" aria-label={t('dashboard.filters.title')}>
      {visibleFilters.map((filter) => renderFilter(filter, values[filter.key] ?? filter.defaultValue ?? '', onChange, t))}
    </section>
  );
}

function renderFilter(filter: UiScreenFilter, value: string, onChange: (key: string, value: string) => void, t: (key: string) => string) {
  const label = translatedLabel(filter.labelKey, filter.label, t);
  if (filter.type === 'select') {
    return (
      <BlueSelect
        key={filter.key}
        name={filter.key}
        label={label}
        value={value}
        options={filter.options ?? []}
        onChange={(event) => onChange(filter.key, event.target.value)}
      />
    );
  }

  return (
    <BlueInput
      key={filter.key}
      name={filter.key}
      label={label}
      type={filter.type === 'number' ? 'number' : 'date'}
      min={filter.min ?? undefined}
      max={filter.max ?? undefined}
      value={value}
      onChange={(event) => onChange(filter.key, event.target.value)}
    />
  );
}
