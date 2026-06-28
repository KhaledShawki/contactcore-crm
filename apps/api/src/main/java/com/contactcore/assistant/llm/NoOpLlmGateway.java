// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.llm;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "contactcore.assistant", name = "provider", havingValue = "noop", matchIfMissing = true)
public class NoOpLlmGateway implements LlmGateway {
    @Override
    public LlmResponse complete(LlmRequest request) {
        String answer = switch (request.retrievalType()) {
            case STALE_LEADS -> "I found leads that may need follow-up. Review the referenced lead records and prioritize the oldest updates first.";
            case LEADS_WITHOUT_CONTACTS -> "I found leads with no active contact person. Add at least one contact person before follow-up work continues.";
            case MARKETING_SOURCE_ANALYTICS -> "I summarized marketing-source performance from the available CRM records. Review sources with high lead volume and low qualification.";
            case CRM_SEARCH -> "I found CRM records matching your request. Open the referenced records for the underlying details.";
            case BUSINESS_PARTNER_DETAILS -> "I found matching CRM records with compact business-partner details. Review the referenced record cards for contact, status, and document information.";
            case CONTACT_COVERAGE -> "I summarized CRM contact-person coverage. Review record kinds with missing contacts first.";
            case RECENT_RECORDS -> "I found recently created active CRM records. Review the referenced records for details.";
            case STATUS_BREAKDOWN -> "I summarized active CRM records by kind and status.";
            case LEAD_PIPELINE -> "I summarized the lead pipeline by status, stale leads, and missing-contact counts.";
            case MULTI_TOOL -> "I used multiple CRM tools to answer your question from active ContactCore data. Review the referenced records and metrics.";
            case ASSISTANT_HELP -> "I can help with read-only ContactCore CRM questions such as searching records, summarizing CRM status, and reviewing lead follow-up or contact coverage.";
            case CRM_SUMMARY -> "I summarized the active CRM data currently available in ContactCore.";
        };
        return new LlmResponse(answer, "noop-assistant");
    }
}
