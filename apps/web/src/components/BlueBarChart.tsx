// Copyright (c) Khaled Shawki. All rights reserved.

import { DirectionalText } from '../i18n/components/DirectionalText';
import { useLocale } from '../i18n/LocaleProvider';
import { formatNumber } from '../i18n/formatters';
import type { ChartPoint, MonthlyCountPoint } from '../schema/types';

type Point = ChartPoint | MonthlyCountPoint;

interface Props {
  title: string;
  description?: string;
  points: Point[];
  labelKey?: 'label' | 'month';
}

export default function BlueBarChart({ title, description, points, labelKey = 'label' }: Props) {
  const { locale, t } = useLocale();
  const max = Math.max(...points.map((point) => point.value), 0);

  return (
    <section className="chart-card">
      <header>
        <h2>{title}</h2>
        {description && <p>{description}</p>}
      </header>
      {points.length === 0 ? <div className="empty-state">{t('charts.empty')}</div> : (
        <ul className="bar-chart" aria-label={title}>
          {points.map((point) => {
            const label = labelKey === 'month' ? (point as MonthlyCountPoint).month : (point as ChartPoint).label;
            const width = max === 0 ? 0 : Math.max((point.value / max) * 100, 4);
            return (
              <li className="bar-row" key={label}>
                <span className="bar-label"><DirectionalText value={label} /></span>
                <div className="bar-track">
                  <div className="bar-fill" style={{ width: `${width}%` }} />
                </div>
                <strong>{formatNumber(point.value, locale)}</strong>
              </li>
            );
          })}
        </ul>
      )}
    </section>
  );
}
