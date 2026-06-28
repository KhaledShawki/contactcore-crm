// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import type { AssistantReference } from './assistantTypes';

function referenceRoute(reference: AssistantReference): string {
  return reference.route;
}

describe('assistant types', () => {
  it('keeps record references navigable', () => {
    expect(referenceRoute({ entityType: 'BUSINESS_PARTNER', entityId: 7, label: 'LED-007', route: '/leads/7' })).toBe('/leads/7');
  });
});
