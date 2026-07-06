// Copyright (c) Khaled Shawki. All rights reserved.

import { DirectionalText } from '../i18n/components/DirectionalText';
import { useLocale } from '../i18n/LocaleProvider';
import { formatNumber } from '../i18n/formatters';
import type { ChartPoint } from '../schema/types';

interface Props {
  title: string;
  description?: string;
  points: ChartPoint[];
}

export default function BlueLineChart({ title, description, points }: Props) {
  const { locale, t } = useLocale();
  const values = points.map((point) => point.value);
  const max = Math.max(...values, 0);
  const min = Math.min(...values, 0);
  const range = max - min || 1;
  const path = buildPath(points, min, range);

  return (
    <section className="chart-card">
      <header>
        <h2>{title}</h2>
        {description && <p>{description}</p>}
      </header>
      {points.length === 0 ? <div className="empty-state">{t('charts.empty')}</div> : (
        <div className="line-chart" aria-label={title}>
          <svg viewBox="0 0 100 42" role="img" aria-hidden="true" focusable="false">
            <path className="line-chart__grid" d="M0 6 H100 M0 21 H100 M0 36 H100" />
            <path className="line-chart__path" d={path} />
            {points.map((point, index) => {
              const coordinates = pointCoordinates(index, points.length, point.value, min, range);
              return <circle key={`${point.label}-${index}`} className="line-chart__point" cx={coordinates.x} cy={coordinates.y} r="1.8" />;
            })}
          </svg>
          <div className="line-chart__summary">
            <span><DirectionalText value={points[0]?.label ?? ''} /></span>
            <strong>{formatNumber(max, locale)}</strong>
            <span><DirectionalText value={points[points.length - 1]?.label ?? ''} /></span>
          </div>
        </div>
      )}
    </section>
  );
}

function buildPath(points: ChartPoint[], min: number, range: number): string {
  return points.map((point, index) => {
    const coordinates = pointCoordinates(index, points.length, point.value, min, range);
    return `${index === 0 ? 'M' : 'L'} ${coordinates.x.toFixed(2)} ${coordinates.y.toFixed(2)}`;
  }).join(' ');
}

function pointCoordinates(index: number, count: number, value: number, min: number, range: number): { x: number; y: number } {
  const x = count <= 1 ? 50 : (index / (count - 1)) * 100;
  const y = 36 - ((value - min) / range) * 30;
  return { x, y };
}
