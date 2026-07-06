// Copyright (c) Khaled Shawki. All rights reserved.

import SchemaSectionRenderer from './SchemaSectionRenderer';
import type { UiScreenLayout } from '../schema/types';

interface Props {
  layout: UiScreenLayout;
  dataSources: Record<string, unknown>;
}

export default function SchemaLayoutRenderer({ layout, dataSources }: Props) {
  return (
    <div className={`schema-layout schema-layout--${layout.type}`}>
      {layout.sections.map((section) => (
        <SchemaSectionRenderer key={section.key} section={section} dataSources={dataSources} />
      ))}
    </div>
  );
}
