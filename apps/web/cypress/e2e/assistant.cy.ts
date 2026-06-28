// Copyright (c) Khaled Shawki. All rights reserved.

describe('Assistant', () => {
  it('answers a CRM question and displays record references', () => {
    cy.loginAsAdmin();

    cy.contains('a', 'Assistant').click();
    cy.contains('h1', 'ContactCore Assistant').should('be.visible');
    cy.contains('button', 'Which leads need follow-up?').click();

    cy.contains(/Assistant/i, { timeout: 10_000 }).should('be.visible');
    cy.contains(/follow-up/i, { timeout: 10_000 }).should('be.visible');
  });
});
