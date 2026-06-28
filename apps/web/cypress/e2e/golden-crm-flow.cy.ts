// Copyright (c) Khaled Shawki. All rights reserved.

describe('CRM golden flow', () => {
	it('creates a lead, adds a contact person, and shows dashboard/report pages', () => {
		cy.loginAsAdmin();

		cy.contains('a', 'Leads').click();
		cy.contains('a', 'New').click();

		const suffix = Date.now().toString().slice(-6);

		cy.get('select[name="statusCode"]').select('NEW');
		cy.get('input[name="code"]').type(`E2E-LED-${suffix}`);
		cy.get('input[name="name"]').type(`E2E Lead ${suffix}`);
		cy.get('input[name="primaryEmail"]').type(
			`lead.${suffix}@example.test`,
		);

		cy.contains('button', /^save$/i).click();

		cy.contains(/contact persons/i).should('be.visible');

		cy.get('input[name="firstName"]').type('Mira');
		cy.get('input[name="lastName"]').type('Meyer');
		cy.get('input[name="roleTitle"]').type('Decision Maker');
		cy.get('input[name="email"]').type(`mira.${suffix}@example.test`);

		cy.contains('button', /add contact/i).click();
		cy.contains('Mira Meyer').should('be.visible');

		cy.contains('a', 'Dashboard').click();
		cy.contains(/contact persons/i).should('be.visible');

		cy.contains('a', 'Reports').click();
		cy.contains(/marketing source performance/i).should('be.visible');
	});
});