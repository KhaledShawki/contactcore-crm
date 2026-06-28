// Copyright (c) Khaled Shawki. All rights reserved.

import type { ChartPoint, MonthlyCountPoint } from '../schema/types';

type Point = ChartPoint | MonthlyCountPoint;

interface Props {
  title: string;
  description?: string;
  points: Point[];
  labelKey?: 'label' | 'month';
}

export default function BlueBarChart({ title, description, points, labelKey = 'label' }: Props) {
  const max = Math.max(...points.map((point) => point.value), 0);

  return (
    <section className="chart-card">
      <header>
        <h2>{title}</h2>
        {description && <p>{description}</p>}
      </header>
      {points.length === 0 ? <div className="empty-state">No chart data yet.</div> : (
        <ul className="bar-chart" aria-label={title}>
          {points.map((point) => {
            const label = labelKey === 'month' ? (point as MonthlyCountPoint).month : (point as ChartPoint).label;
            const width = max === 0 ? 0 : Math.max((point.value / max) * 100, 4);
            return (
              <li className="bar-row" key={label}>
                <span className="bar-label">{label}</span>
                <div className="bar-track">
                  <div className="bar-fill" style={{ width: `${width}%` }} />
                </div>
                <strong>{point.value}</strong>
              </li>
            );
          })}
        </ul>
      )}
    </section>
  );
}
