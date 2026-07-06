// Copyright (c) Khaled Shawki. All rights reserved.

import SchemaSectionRenderer from './SchemaSectionRenderer';
import type { UiScreenLayout } from '../schema/types';
import type { SchemaDataSourceEntry } from './useSchemaDataSources';

interface Props {
  layout: UiScreenLayout;
  dataSources: Record<string, unknown>;
  dataSourceEntries: Record<string, SchemaDataSourceEntry>;
}

export default function SchemaLayoutRenderer({ layout, dataSources, dataSourceEntries }: Props) {
  return (
    <div className={`schema-layout schema-layout--${layout.type}`}>
      {layout.sections.map((section) => (
        <SchemaSectionRenderer key={section.key} section={section} dataSources={dataSources} dataSourceEntries={dataSourceEntries} />
      ))}
    </div>
  );
}
