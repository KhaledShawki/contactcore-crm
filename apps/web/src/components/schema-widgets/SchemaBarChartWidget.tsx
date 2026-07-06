// Copyright (c) Khaled Shawki. All rights reserved.

import BlueBarChart from '../BlueBarChart';
import type { ChartPoint, UiWidget } from '../../schema/types';
import { useLocale } from '../../i18n/LocaleProvider';
import { asArray, readNumber, readPath, readString } from '../../schema-renderer/schemaData';
import { translatedLabel, translateDataLabel } from '../../schema-renderer/schemaLabels';

interface Props {
  widget: UiWidget;
  source: unknown;
}

export default function SchemaBarChartWidget({ widget, source }: Props) {
  const { t } = useLocale();
  const labelKey = widget.bindings?.label ?? 'label';
  const valueKey = widget.bindings?.value ?? 'value';
  const points = asArray<Record<string, unknown>>(readPath(source, widget.dataPath)).map<ChartPoint>((row) => ({
    label: translateDataLabel(readString(row, labelKey), t),
    value: readNumber(row, valueKey),
  }));

  return (
    <BlueBarChart
      title={translatedLabel(widget.titleKey, widget.title, t)}
      description={translatedLabel(widget.descriptionKey, widget.description, t)}
      points={points}
    />
  );
}
