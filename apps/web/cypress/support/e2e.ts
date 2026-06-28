// Copyright (c) Khaled Shawki. All rights reserved.

Cypress.Commands.add('loginAsAdmin', () => {
  return cy
    .env(
      ['CONTACTCORE_ADMIN_USERNAME', 'CONTACTCORE_ADMIN_PASSWORD'],
      { log: false },
    )
    .then((environment) => {
      const username = environment.CONTACTCORE_ADMIN_USERNAME || 'admin';
      const password = environment.CONTACTCORE_ADMIN_PASSWORD || 'change-this-password';

      cy.visit('/login');
      cy.contains('label', 'Username').find('input').clear().type(username, { log: false });
      cy.contains('label', 'Password').find('input').clear().type(password, { log: false });
      cy.contains('button', 'Sign in').click();

      cy.location('pathname', { timeout: 10_000 }).should('equal', '/dashboard');
      cy.get('nav[aria-label="Main navigation"]', { timeout: 10_000 }).should('be.visible');
    });
});
