// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.application;

import com.contactcore.crm.domain.LeadSourceRepository;
import com.contactcore.schema.api.UiCapabilityReference;
import com.contactcore.schema.api.UiField;
import com.contactcore.schema.api.UiFormRule;
import com.contactcore.schema.api.UiManifest;
import com.contactcore.schema.api.UiResourceCapabilities;
import com.contactcore.schema.api.UiRoute;
import com.contactcore.schema.api.UiScreen;
import com.contactcore.schema.api.UiValidation;
import com.contactcore.shared.api.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UiSchemaService {
    private static final String PHONE_PATTERN = "^[+()0-9\\s./-]{3,64}$";
    private static final String PHONE_MESSAGE = "Use digits, spaces, +, -, /, ., or parentheses only.";
    private static final String CODE_PATTERN = "^[A-Z0-9][A-Z0-9_-]{1,63}$";
    private static final String CODE_MESSAGE = "Use uppercase letters, numbers, underscore, or dash.";
    private static final String COUNTRY_CODE_PATTERN = "^[A-Z]{2}$";
    private static final String COUNTRY_CODE_MESSAGE = "Use a two-letter uppercase country code, for example DE or CH.";
    private static final List<String> STATUS_OPTIONS = List.of("NEW", "ACTIVE", "QUALIFIED", "INACTIVE");
    private static final List<String> LOCALE_OPTIONS = List.of("en", "de", "ar");
    private static final List<String> TIMEZONE_OPTIONS = List.of("Europe/Berlin", "Europe/Zurich", "UTC");

    private static final String MARKETING_SOURCE_RESOURCE = "marketing.source";
    private static final String PROFILE_RESOURCE = "profile.user";

    private final LeadSourceRepository leadSources;
    private final UiCapabilityResolver capabilityResolver;

    public UiSchemaService(LeadSourceRepository leadSources, UiCapabilityResolver capabilityResolver) {
        this.leadSources = leadSources;
        this.capabilityResolver = capabilityResolver;
    }

    public UiManifest manifest() {
        UiCapabilitySnapshot capabilities = capabilityResolver.resolveCurrentSubjectCapabilities();
        return new UiManifest("ContactCore CRM", List.of(
                route("/dashboard", "Dashboard", "navigation.dashboard", "dashboard", null, capabilities),
                route("/customers", "Customers", "navigation.customers", "customers", capability(UiResourceKeys.CRM_BUSINESS_PARTNER, UiCapabilityKeys.LIST), capabilities),
                route("/leads", "Leads", "navigation.leads", "leads", capability(UiResourceKeys.CRM_BUSINESS_PARTNER, UiCapabilityKeys.LIST), capabilities),
                route("/suppliers", "Suppliers", "navigation.suppliers", "suppliers", capability(UiResourceKeys.CRM_BUSINESS_PARTNER, UiCapabilityKeys.LIST), capabilities),
                route("/marketing-sources", "Marketing Sources", "navigation.marketingSources", "marketingSources", null, capabilities),
                route("/reports", "Reports", "navigation.reports", "reports", null, capabilities),
                route("/assistant", "Assistant", "navigation.assistant", "assistant", capability(UiResourceKeys.ASSISTANT_SESSION, UiCapabilityKeys.ASK), capabilities),
                route("/settings", "Settings", "navigation.settings", "settings", null, capabilities),
                route("/profile", "Profile", "navigation.profile", "profile", null, capabilities)
        ), capabilities.asList());
    }

    public UiScreen screen(String key) {
        UiCapabilitySnapshot capabilities = capabilityResolver.resolveCurrentSubjectCapabilities();
        return switch (key) {
            case "customers" -> crmScreen("customers", "Customers", "CUSTOMER", "ACTIVE", capabilities.resource(UiResourceKeys.CRM_BUSINESS_PARTNER));
            case "leads" -> crmScreen("leads", "Leads", "LEAD", "NEW", capabilities.resource(UiResourceKeys.CRM_BUSINESS_PARTNER));
            case "suppliers" -> crmScreen("suppliers", "Suppliers", "SUPPLIER", "ACTIVE", capabilities.resource(UiResourceKeys.CRM_BUSINESS_PARTNER));
            case "marketingSources" -> marketingSourceScreen();
            case "contactPersons" -> contactPersonScreen(capabilities.resource(UiResourceKeys.CRM_BUSINESS_PARTNER));
            case "profile" -> profileScreen();
            default -> throw new NotFoundException("UI screen not found: " + key);
        };
    }

    private UiScreen crmScreen(String key, String title, String kind, String defaultStatus, UiResourceCapabilities capabilities) {
        return new UiScreen(
                key,
                title,
                kind,
                "/crm/business-partners?kind=" + kind,
                "/crm/business-partners/{id}",
                "/crm/business-partners",
                "/crm/business-partners/{id}",
                "/crm/business-partners/{id}",
                "/crm/business-partners/{id}/documents",
                List.of(
                        hidden("kind", kind),
                        select("statusCode", "Status", true, true, true, defaultStatus, STATUS_OPTIONS, requiredSelectHelp("Select the lifecycle status.")),
                        text("code", "Code", true, true, true, validation("text", 2, 64, CODE_PATTERN, CODE_MESSAGE, null, null, "Stable public code. Example: ACME-001.")),
                        text("name", "Name", true, true, true, validation("text", 2, 255, null, null, null, null, "Legal or display name.")),
                        text("primaryEmail", "Email", false, true, true, validation("email", null, 255, null, null, null, null, "Primary generic email address.")),
                        text("primaryPhone", "Phone", false, true, true, validation("tel", null, 64, PHONE_PATTERN, PHONE_MESSAGE, null, null, null)),
                        text("website", "Website", false, false, true, validation("url", null, 255, "^$|^https?://.+", "Website must start with http:// or https://.", null, null, null)),
                        select("sourceCode", "Marketing Source", false, false, true, "", marketingSourceOptions(), UiValidation.none()),
                        text("addressLine1", "Address line 1", false, false, true, maxText(255)),
                        text("addressLine2", "Address line 2", false, false, true, maxText(255)),
                        text("city", "City", false, true, true, validation("text", null, 128, null, null, null, null, null)),
                        text("postalCode", "Postal code", false, false, true, validation("text", null, 64, null, null, null, null, null)),
                        text("countryCode", "Country code", false, true, true, validation("text", null, 2, COUNTRY_CODE_PATTERN, COUNTRY_CODE_MESSAGE, null, null, "Two-letter ISO code.")),
                        textarea("notes", "Notes", false, false, true, validation("textarea", null, 5000, null, null, null, null, null))
                ),
                List.of(),
                capabilities
        );
    }

    private static UiScreen marketingSourceScreen() {
        return new UiScreen(
                "marketingSources",
                "Marketing Sources",
                "MARKETING_SOURCE",
                "/marketing/sources",
                "/marketing/sources/{id}",
                "/marketing/sources",
                "/marketing/sources/{id}",
                "/marketing/sources/{id}",
                "",
                List.of(
                        text("code", "Code", true, true, true, validation("text", 2, 64, CODE_PATTERN, CODE_MESSAGE, null, null, "Stable source code. Example: LINKEDIN.")),
                        text("name", "Name", true, true, true, validation("text", 2, 120, null, null, null, null, null)),
                        number("sortOrder", "Sort order", false, true, true, validation("number", null, null, null, null, 0, 10000, "Lower values appear first."))
                ),
                List.of(),
                UiResourceCapabilities.empty(MARKETING_SOURCE_RESOURCE)
        );
    }

    private static UiScreen contactPersonScreen(UiResourceCapabilities capabilities) {
        return new UiScreen(
                "contactPersons",
                "Contact Persons",
                "CONTACT_PERSON",
                "",
                "",
                "",
                "",
                "",
                "",
                List.of(
                        text("firstName", "First name", true, true, true, validation("text", 1, 120, null, null, null, null, null)),
                        text("lastName", "Last name", true, true, true, validation("text", 1, 120, null, null, null, null, null)),
                        text("roleTitle", "Role", false, true, true, validation("text", null, 160, null, null, null, null, null)),
                        text("email", "Email", false, true, true, validation("email", null, 255, null, null, null, null, null)),
                        text("phone", "Phone", false, true, true, validation("tel", null, 64, PHONE_PATTERN, PHONE_MESSAGE, null, null, null)),
                        text("mobile", "Mobile", false, false, true, validation("tel", null, 64, PHONE_PATTERN, PHONE_MESSAGE, null, null, null)),
                        text("department", "Department", false, false, true, validation("text", null, 120, null, null, null, null, null)),
                        checkbox("primaryContact", "Primary contact", false, false, true, UiValidation.none()),
                        textarea("notes", "Notes", false, false, true, validation("textarea", null, 5000, null, null, null, null, null))
                ),
                List.of(new UiFormRule("atLeastOne", List.of("email", "phone", "mobile"), "Add at least one email, phone, or mobile number.")),
                capabilities
        );
    }

    private static UiScreen profileScreen() {
        return new UiScreen(
                "profile",
                "User Profile",
                "PROFILE",
                "/profile",
                "/profile",
                "/profile",
                "/profile",
                "",
                "/profile/image",
                List.of(
                        text("username", "Username", false, false, false, true, null, maxText(255)),
                        text("email", "Email", false, true, false, true, null, validation("email", null, 255, null, null, null, null, null)),
                        text("displayName", "Display name", true, true, true, validation("text", 2, 255, null, null, null, null, null)),
                        text("phone", "Phone", false, false, true, validation("tel", null, 64, PHONE_PATTERN, PHONE_MESSAGE, null, null, null)),
                        text("jobTitle", "Job title", false, false, true, validation("text", null, 128, null, null, null, null, null)),
                        select("locale", "Locale", true, false, true, "en", LOCALE_OPTIONS, requiredSelectHelp("Preferred UI language.")),
                        select("timezone", "Timezone", true, false, true, "Europe/Berlin", TIMEZONE_OPTIONS, requiredSelectHelp("Used for date and time display.")),
                        textarea("bio", "Bio", false, false, true, validation("textarea", null, 5000, null, null, null, null, null))
                ),
                List.of(),
                UiResourceCapabilities.empty(PROFILE_RESOURCE)
        );
    }

    private static UiRoute route(String path, String label, String labelKey, String screenKey,
                                 UiCapabilityReference requiredCapability, UiCapabilitySnapshot capabilities) {
        return new UiRoute(path, label, labelKey, screenKey, requiredCapability, capabilities.allows(requiredCapability));
    }

    private static UiCapabilityReference capability(String resourceKey, String capability) {
        return UiCapabilityReference.of(resourceKey, capability);
    }

    private List<String> marketingSourceOptions() {
        List<String> options = new ArrayList<>();
        options.add("");
        leadSources.findActiveOrdered().forEach(source -> options.add(source.getCode()));
        return options;
    }

    private static UiField hidden(String key, String defaultValue) {
        return new UiField(key, key, labelKey(key), valueKind(key, "hidden"), "hidden", true, false, false, false, defaultValue, List.of(), UiValidation.none());
    }

    private static UiField text(String key, String label, boolean required, boolean listVisible, boolean formVisible) {
        return text(key, label, required, listVisible, formVisible, false, null, UiValidation.none());
    }

    private static UiField text(String key, String label, boolean required, boolean listVisible, boolean formVisible, UiValidation validation) {
        return text(key, label, required, listVisible, formVisible, false, null, validation);
    }

    private static UiField text(String key, String label, boolean required, boolean listVisible, boolean formVisible, boolean readOnly, String defaultValue) {
        return text(key, label, required, listVisible, formVisible, readOnly, defaultValue, UiValidation.none());
    }

    private static UiField text(String key, String label, boolean required, boolean listVisible, boolean formVisible, boolean readOnly, String defaultValue, UiValidation validation) {
        return new UiField(key, label, labelKey(key), valueKind(key, "text"), "text", required, listVisible, formVisible, readOnly, defaultValue, List.of(), validation);
    }

    private static UiField textarea(String key, String label, boolean required, boolean listVisible, boolean formVisible) {
        return textarea(key, label, required, listVisible, formVisible, UiValidation.none());
    }

    private static UiField textarea(String key, String label, boolean required, boolean listVisible, boolean formVisible, UiValidation validation) {
        return new UiField(key, label, labelKey(key), valueKind(key, "textarea"), "textarea", required, listVisible, formVisible, false, null, List.of(), validation);
    }

    private static UiField number(String key, String label, boolean required, boolean listVisible, boolean formVisible, UiValidation validation) {
        return new UiField(key, label, labelKey(key), valueKind(key, "number"), "number", required, listVisible, formVisible, false, null, List.of(), validation);
    }

    private static UiField checkbox(String key, String label, boolean required, boolean listVisible, boolean formVisible, UiValidation validation) {
        return new UiField(key, label, labelKey(key), valueKind(key, "checkbox"), "checkbox", required, listVisible, formVisible, false, null, List.of(), validation);
    }

    private static UiField select(String key, String label, boolean required, boolean listVisible, boolean formVisible, String defaultValue, List<String> options) {
        return select(key, label, required, listVisible, formVisible, defaultValue, options, UiValidation.none());
    }

    private static UiField select(String key, String label, boolean required, boolean listVisible, boolean formVisible, String defaultValue, List<String> options, UiValidation validation) {
        return new UiField(key, label, labelKey(key), valueKind(key, "select"), "select", required, listVisible, formVisible, false, defaultValue, options, validation);
    }


    private static String labelKey(String key) {
        return "schema.field." + key;
    }

    private static String valueKind(String key, String fieldType) {
        return switch (key) {
            case "code", "countryCode", "sourceCode", "statusCode", "kind", "timezone", "locale" -> "code";
            case "email", "primaryEmail" -> "email";
            case "phone", "primaryPhone", "mobile" -> "phone";
            case "website" -> "url";
            case "sortOrder" -> "number";
            case "addressLine1", "addressLine2", "city", "postalCode", "name", "displayName", "firstName", "lastName", "bio", "notes" -> "sourceText";
            default -> fieldType.equals("number") ? "number" : "text";
        };
    }

    private static UiValidation maxText(int maxLength) {
        return validation("text", null, maxLength, null, null, null, null, null);
    }

    private static UiValidation requiredSelectHelp(String helpText) {
        return validation("select", null, null, null, null, null, null, helpText);
    }

    private static UiValidation validation(
            String inputType,
            Integer minLength,
            Integer maxLength,
            String pattern,
            String patternMessage,
            Integer minNumber,
            Integer maxNumber,
            String helpText
    ) {
        return new UiValidation(minLength, maxLength, pattern, patternMessage, inputType, minNumber, maxNumber, helpText);
    }
}
