// Copyright (c) Khaled Shawki. All rights reserved.

import { describe, expect, it } from 'vitest';
import { capabilityReferenceIsAllowed, findCapabilitySet, hasCapability, routeIsVisible } from './capabilities';

const capabilitySets = [
  { resourceKey: 'crm.businessPartner', capabilities: { list: true, create: false } },
  { resourceKey: 'assistant.session', capabilities: { ask: true } },
];

describe('schema capabilities', () => {
  it('checks capability values from a resource capability set', () => {
    expect(hasCapability(capabilitySets[0], 'list')).toBe(true);
    expect(hasCapability(capabilitySets[0], 'create')).toBe(false);
    expect(hasCapability(undefined, 'list')).toBe(false);
  });

  it('finds resource capability sets by key', () => {
    expect(findCapabilitySet(capabilitySets, 'assistant.session')?.capabilities.ask).toBe(true);
    expect(findCapabilitySet(capabilitySets, 'commercial.document')).toBeUndefined();
  });

  it('treats routes as visible unless explicitly hidden', () => {
    expect(routeIsVisible({})).toBe(true);
    expect(routeIsVisible({ visible: true })).toBe(true);
    expect(routeIsVisible({ visible: false })).toBe(false);
  });

  it('evaluates route capability references against manifest capabilities', () => {
    expect(capabilityReferenceIsAllowed(capabilitySets, { resourceKey: 'crm.businessPartner', capability: 'list' })).toBe(true);
    expect(capabilityReferenceIsAllowed(capabilitySets, { resourceKey: 'crm.businessPartner', capability: 'create' })).toBe(false);
    expect(capabilityReferenceIsAllowed(capabilitySets, null)).toBe(true);
  });
});
