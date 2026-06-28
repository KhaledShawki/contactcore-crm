// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.answer;

import org.springframework.stereotype.Component;

@Component
public class AssistantStaticResponseCatalog {
    public String greetingAnswer() {
        return "Hi. I can help you search CRM records, summarize customers, leads, and suppliers, check contact coverage, review lead follow-up, and answer read-only questions based on ContactCore CRM data.";
    }

    public String identityAnswer() {
        return """
                I am ContactCore Assistant, a read-only CRM assistant for ContactCore.

                I can help you search and summarize authorized CRM data such as customers, leads, suppliers, contact persons, marketing sources, recent records, follow-up leads, and missing contact-person coverage.

                I cannot create, update, delete, archive, or send records.
                """.trim();
    }

    public String capabilitiesAnswer() {
        return """
                I can help with read-only CRM questions, for example:

                - Find customers, leads, or suppliers by name.
                - Check whether a customer, lead, or supplier exists.
                - List leads that need follow-up.
                - List leads missing contact persons.
                - Summarize CRM status.
                - Show marketing source performance.
                - Show recent CRM records.
                - Summarize contact coverage.

                I cannot create, update, delete, archive, or send records.
                """.trim();
    }

    public String unsupportedAnswer() {
        return """
                I can only answer from authorized ContactCore CRM data in read-only mode.

                I cannot create, update, delete, archive, send messages, or perform other write actions.

                Suggested next manual action:
                Open the relevant CRM record and perform the change manually if you have the required permission.
                """.trim();
    }

    public String unclearRequestAnswer() {
        return "I could not understand the request. Ask me about customers, leads, suppliers, contact coverage, CRM summaries, marketing-source performance, recent records, or specific CRM records.";
    }
}
