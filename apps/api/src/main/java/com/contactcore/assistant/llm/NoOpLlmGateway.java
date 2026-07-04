// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.llm;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "contactcore.assistant", name = "provider", havingValue = "noop", matchIfMissing = true)
public class NoOpLlmGateway implements LlmGateway {
    @Override
    public LlmResponse complete(LlmRequest request) {
        String locale = request.locale() == null ? "en" : request.locale();
        String answer = switch (locale) {
            case "de" -> germanAnswer(request);
            case "ar" -> arabicAnswer(request);
            default -> englishAnswer(request);
        };
        return new LlmResponse(answer, "noop-assistant");
    }

    private String englishAnswer(LlmRequest request) {
        return switch (request.retrievalType()) {
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
    }

    private String germanAnswer(LlmRequest request) {
        return switch (request.retrievalType()) {
            case STALE_LEADS -> "Ich habe Leads gefunden, die möglicherweise ein Follow-up benötigen. Prüfen Sie zuerst die ältesten referenzierten Leads.";
            case LEADS_WITHOUT_CONTACTS -> "Ich habe Leads ohne aktive Kontaktperson gefunden. Fügen Sie vor der weiteren Bearbeitung mindestens eine Kontaktperson hinzu.";
            case MARKETING_SOURCE_ANALYTICS -> "Ich habe die Marketingquellen-Performance aus den verfügbaren CRM-Daten zusammengefasst.";
            case CRM_SEARCH -> "Ich habe CRM-Datensätze gefunden, die zu Ihrer Anfrage passen. Öffnen Sie die Referenzen für die Details.";
            case BUSINESS_PARTNER_DETAILS -> "Ich habe passende CRM-Datensätze mit kompakten Geschäftspartnerdetails gefunden. Prüfen Sie die referenzierten Datensätze.";
            case CONTACT_COVERAGE -> "Ich habe die Kontaktpersonen-Abdeckung zusammengefasst. Prüfen Sie zuerst Kategorien mit fehlenden Kontakten.";
            case RECENT_RECORDS -> "Ich habe kürzlich erstellte aktive CRM-Datensätze gefunden. Prüfen Sie die referenzierten Datensätze.";
            case STATUS_BREAKDOWN -> "Ich habe aktive CRM-Datensätze nach Art und Status zusammengefasst.";
            case LEAD_PIPELINE -> "Ich habe die Lead-Pipeline nach Status, überfälligen Leads und fehlenden Kontakten zusammengefasst.";
            case MULTI_TOOL -> "Ich habe mehrere CRM-Tools verwendet, um aus aktiven ContactCore-Daten zu antworten. Prüfen Sie die referenzierten Datensätze und Kennzahlen.";
            case ASSISTANT_HELP -> "Ich kann bei schreibgeschützten ContactCore-CRM-Fragen helfen, zum Beispiel bei Suche, CRM-Status, Lead-Follow-up und Kontaktabdeckung.";
            case CRM_SUMMARY -> "Ich habe die derzeit in ContactCore verfügbaren aktiven CRM-Daten zusammengefasst.";
        };
    }

    private String arabicAnswer(LlmRequest request) {
        return switch (request.retrievalType()) {
            case STALE_LEADS -> "وجدت عملاء محتملين قد يحتاجون إلى متابعة. راجع سجلات العملاء المشار إليها وابدأ بالأقدم.";
            case LEADS_WITHOUT_CONTACTS -> "وجدت عملاء محتملين بدون جهة اتصال نشطة. أضف جهة اتصال واحدة على الأقل قبل المتابعة.";
            case MARKETING_SOURCE_ANALYTICS -> "لخصت أداء مصادر التسويق من سجلات CRM المتاحة.";
            case CRM_SEARCH -> "وجدت سجلات CRM مطابقة لطلبك. افتح المراجع للاطلاع على التفاصيل.";
            case BUSINESS_PARTNER_DETAILS -> "وجدت سجلات CRM مطابقة مع تفاصيل مختصرة للشريك التجاري. راجع السجلات المشار إليها.";
            case CONTACT_COVERAGE -> "لخصت تغطية جهات الاتصال في CRM. راجع أولاً الأنواع التي تنقصها جهات الاتصال.";
            case RECENT_RECORDS -> "وجدت سجلات CRM نشطة تم إنشاؤها حديثاً. راجع السجلات المشار إليها.";
            case STATUS_BREAKDOWN -> "لخصت سجلات CRM النشطة حسب النوع والحالة.";
            case LEAD_PIPELINE -> "لخصت مسار العملاء المحتملين حسب الحالة والعملاء الذين يحتاجون إلى متابعة ونقص جهات الاتصال.";
            case MULTI_TOOL -> "استخدمت عدة أدوات CRM للإجابة من بيانات ContactCore النشطة. راجع السجلات والمقاييس المشار إليها.";
            case ASSISTANT_HELP -> "يمكنني المساعدة في أسئلة ContactCore CRM للقراءة فقط، مثل البحث والتلخيص ومراجعة المتابعة وتغطية جهات الاتصال.";
            case CRM_SUMMARY -> "لخصت بيانات CRM النشطة المتاحة حالياً في ContactCore.";
        };
    }
}
