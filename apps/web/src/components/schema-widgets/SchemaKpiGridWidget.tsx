// Copyright (c) Khaled Shawki. All rights reserved.

import BlueKpiGrid from '../BlueKpiGrid';
import type { KpiMetric, UiWidget } from '../../schema/types';
import { asArray, readPath } from '../../schema-renderer/schemaData';

interface Props {
  widget: UiWidget;
  source: unknown;
}

export default function SchemaKpiGridWidget({ widget, source }: Props) {
  const metrics = asArray<KpiMetric>(readPath(source, widget.dataPath));
  return <BlueKpiGrid metrics={metrics} />;
}
