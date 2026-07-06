// Copyright (c) Khaled Shawki. All rights reserved.

import SchemaWidgetRenderer from './SchemaWidgetRenderer';
import { translatedLabel } from './schemaLabels';
import { useLocale } from '../i18n/LocaleProvider';
import type { UiLayoutSection } from '../schema/types';

interface Props {
  section: UiLayoutSection;
  dataSources: Record<string, unknown>;
}

export default function SchemaSectionRenderer({ section, dataSources }: Props) {
  const { t } = useLocale();
  if (section.visible === false) return null;

  const visibleWidgets = section.widgets.filter((widget) => widget.visible !== false);
  if (visibleWidgets.length === 0) return null;

  return (
    <section className="schema-layout-section" data-columns={section.columns}>
      {(section.title || section.titleKey) && (
        <header className="schema-layout-section__header">
          <h2>{translatedLabel(section.titleKey, section.title, t)}</h2>
        </header>
      )}
      <div className="schema-widget-grid" data-columns={section.columns}>
        {visibleWidgets.map((widget) => (
          <SchemaWidgetRenderer key={widget.key} widget={widget} dataSources={dataSources} />
        ))}
      </div>
    </section>
  );
}
