// Copyright (c) Khaled Shawki. All rights reserved.

import { useLocale } from '../i18n/LocaleProvider';
import { formatNumber } from '../i18n/formatters';
import type { KpiMetric } from '../schema/types';

interface Props {
  metrics: KpiMetric[];
}

export default function BlueKpiGrid({ metrics }: Props) {
  const { locale, t } = useLocale();
  return (
    <div className="kpi-grid">
      {metrics.map((metric) => (
        <article className="kpi-card" key={metric.key}>
          <span>{withFallback(t(`analytics.kpi.${metric.key}.label`), `analytics.kpi.${metric.key}.label`, metric.label)}</span>
          <strong>{formatMetric(metric.value, metric.unit, locale)}</strong>
          <p>{withFallback(t(`analytics.kpi.${metric.key}.help`), `analytics.kpi.${metric.key}.help`, metric.helpText)}</p>
        </article>
      ))}
    </div>
  );
}

function formatMetric(value: number, unit: string, locale: string): string {
  if (unit === '%') {
    return `${formatNumber(Number(value.toFixed(1)), locale)}%`;
  }
  const formatted = formatNumber(value, locale);
  return unit ? `${formatted} ${unit}` : formatted;
}

function withFallback(translated: string, key: string, fallback: string): string {
  return translated === key ? fallback : translated;
}
