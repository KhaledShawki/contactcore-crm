// Copyright (c) Khaled Shawki. All rights reserved.

import type { KpiMetric } from '../schema/types';

const wholeNumberFormatter = new Intl.NumberFormat(undefined, { maximumFractionDigits: 0 });

interface Props {
  metrics: KpiMetric[];
}

export default function BlueKpiGrid({ metrics }: Props) {
  return (
    <div className="kpi-grid">
      {metrics.map((metric) => (
        <article className="kpi-card" key={metric.key}>
          <span>{metric.label}</span>
          <strong>{formatMetric(metric.value, metric.unit)}</strong>
          <p>{metric.helpText}</p>
        </article>
      ))}
    </div>
  );
}

function formatMetric(value: number, unit: string): string {
  if (unit === '%') {
    return `${value.toFixed(1)}%`;
  }
  return wholeNumberFormatter.format(value);
}
