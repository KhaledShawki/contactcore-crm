// Copyright (c) Khaled Shawki. All rights reserved.

describe('UI settings', () => {
  it('persists theme, density, text size, contrast, and motion preferences', () => {
    cy.loginAsAdmin();
    cy.contains('a', /ui settings/i).click();
    cy.contains('label', /theme/i).find('select').select('graphite');
    cy.contains('label', /text size/i).find('select').select('large');
    cy.contains('label', /density/i).find('select').select('spacious');
    cy.contains('label', /sidebar/i).find('select').select('compact');
    cy.contains('label', /high contrast/i).find('input').check({ force: true });
    cy.contains('label', /reduce motion/i).find('input').check({ force: true });
    cy.contains('button', /save settings/i).click();
    cy.contains(/ui settings saved/i).should('be.visible');
    cy.get('html').should('have.attr', 'data-theme', 'graphite');
    cy.get('html').should('have.attr', 'data-text-size', 'large');
    cy.get('html').should('have.attr', 'data-density', 'spacious');
  });
});
