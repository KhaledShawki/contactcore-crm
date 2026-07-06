// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.application;

import com.contactcore.dashboard.api.CommercialDashboardSummaryResponse;
import com.contactcore.dashboard.api.InvoiceAgingBucketResponse;
import com.contactcore.dashboard.api.SalesByDocumentTypeResponse;
import com.contactcore.dashboard.api.SalesTrendPointResponse;
import com.contactcore.dashboard.api.TopCustomerResponse;
import com.contactcore.dashboard.api.TopSellingItemResponse;
import com.contactcore.dashboard.api.UnpaidInvoiceCustomerResponse;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CommercialDashboardAggregator {
    private static final List<String> AGING_BUCKET_ORDER = List.of("Not due", "1-30 days", "31-60 days", "61-90 days", "90+ days");

    private final Clock clock;

    public CommercialDashboardAggregator() {
        this(Clock.systemDefaultZone());
    }

    CommercialDashboardAggregator(Clock clock) {
        this.clock = clock;
    }

    public CommercialDashboardSummaryResponse summary(CommercialDashboardSnapshot snapshot, CommercialDashboardQuery query) {
        List<CommercialDashboardDocument> invoices = snapshot.invoices();
        BigDecimal totalSales = sum(invoices, CommercialDashboardDocument::totalAmount);
        BigDecimal openInvoiceAmount = sum(invoices, CommercialDashboardDocument::openAmount);
        BigDecimal overdueAmount = sum(overdueInvoices(invoices), CommercialDashboardDocument::openAmount);
        int unpaidInvoiceCount = (int) invoices.stream().filter(this::unpaid).count();
        Set<String> activeCustomers = invoices.stream()
                .map(CommercialDashboardDocument::businessPartnerCode)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> soldItems = invoices.stream()
                .flatMap(document -> document.lines().stream())
                .map(CommercialDashboardLine::itemCode)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return new CommercialDashboardSummaryResponse(
                totalSales,
                openInvoiceAmount,
                overdueAmount,
                unpaidInvoiceCount,
                activeCustomers.size(),
                soldItems.size(),
                currency(snapshot, query)
        );
    }

    public List<TopSellingItemResponse> topSellingItems(CommercialDashboardSnapshot snapshot, CommercialDashboardQuery query) {
        Map<String, ItemAccumulator> items = new LinkedHashMap<>();
        for (CommercialDashboardDocument invoice : snapshot.invoices()) {
            for (CommercialDashboardLine line : invoice.lines()) {
                if (line.itemCode().isBlank()) {
                    continue;
                }
                items.computeIfAbsent(line.itemCode(), key -> new ItemAccumulator(line.itemCode(), line.itemName()))
                        .add(line.quantity(), line.netAmount());
            }
        }
        return items.values().stream()
                .sorted(Comparator.comparing(ItemAccumulator::netAmount).reversed().thenComparing(ItemAccumulator::itemCode))
                .limit(query.limit())
                .map(item -> new TopSellingItemResponse(item.itemCode(), item.itemName(), item.quantity(), item.netAmount(), currency(snapshot, query)))
                .toList();
    }

    public List<TopCustomerResponse> topCustomers(CommercialDashboardSnapshot snapshot, CommercialDashboardQuery query) {
        Map<String, CustomerAccumulator> customers = new LinkedHashMap<>();
        for (CommercialDashboardDocument invoice : snapshot.invoices()) {
            if (invoice.businessPartnerCode().isBlank()) {
                continue;
            }
            customers.computeIfAbsent(invoice.businessPartnerCode(), key -> new CustomerAccumulator(invoice.businessPartnerCode(), invoice.businessPartnerName()))
                    .add(invoice.totalAmount());
        }
        return customers.values().stream()
                .sorted(Comparator.comparing(CustomerAccumulator::amount).reversed().thenComparing(CustomerAccumulator::businessPartnerCode))
                .limit(query.limit())
                .map(customer -> new TopCustomerResponse(customer.businessPartnerCode(), customer.businessPartnerName(), customer.amount(), currency(snapshot, query)))
                .toList();
    }

    public List<UnpaidInvoiceCustomerResponse> unpaidInvoiceCustomers(CommercialDashboardSnapshot snapshot, CommercialDashboardQuery query) {
        Map<String, UnpaidCustomerAccumulator> customers = new LinkedHashMap<>();
        for (CommercialDashboardDocument invoice : snapshot.invoices()) {
            if (!unpaid(invoice) || invoice.businessPartnerCode().isBlank()) {
                continue;
            }
            customers.computeIfAbsent(invoice.businessPartnerCode(), key -> new UnpaidCustomerAccumulator(invoice.businessPartnerCode(), invoice.businessPartnerName()))
                    .add(invoice, overdueDays(invoice));
        }
        return customers.values().stream()
                .sorted(Comparator.comparing(UnpaidCustomerAccumulator::openAmount).reversed().thenComparing(UnpaidCustomerAccumulator::businessPartnerCode))
                .limit(query.limit())
                .map(customer -> new UnpaidInvoiceCustomerResponse(
                        customer.businessPartnerCode(),
                        customer.businessPartnerName(),
                        customer.openAmount(),
                        currency(snapshot, query),
                        customer.invoiceCount(),
                        customer.oldestDueDate(),
                        customer.maxOverdueDays()
                ))
                .toList();
    }

    public List<InvoiceAgingBucketResponse> invoiceAging(CommercialDashboardSnapshot snapshot, CommercialDashboardQuery query) {
        Map<String, BigDecimal> buckets = AGING_BUCKET_ORDER.stream()
                .collect(Collectors.toMap(Function.identity(), ignored -> BigDecimal.ZERO, BigDecimal::add, LinkedHashMap::new));
        for (CommercialDashboardDocument invoice : snapshot.invoices()) {
            if (unpaid(invoice)) {
                String bucket = agingBucket(overdueDays(invoice));
                buckets.put(bucket, buckets.get(bucket).add(invoice.openAmount()));
            }
        }
        return buckets.entrySet().stream()
                .map(entry -> new InvoiceAgingBucketResponse(entry.getKey(), entry.getValue(), currency(snapshot, query)))
                .toList();
    }

    public List<SalesTrendPointResponse> salesTrend(CommercialDashboardSnapshot snapshot, CommercialDashboardQuery query) {
        Map<String, BigDecimal> periods = new LinkedHashMap<>();
        YearMonth current = YearMonth.from(query.dateRange().from());
        YearMonth end = YearMonth.from(query.dateRange().to());
        while (!current.isAfter(end)) {
            periods.put(current.toString(), BigDecimal.ZERO);
            current = current.plusMonths(1);
        }
        for (CommercialDashboardDocument invoice : snapshot.invoices()) {
            if (invoice.documentDate() != null) {
                String period = YearMonth.from(invoice.documentDate()).toString();
                periods.computeIfPresent(period, (ignored, amount) -> amount.add(invoice.totalAmount()));
            }
        }
        return periods.entrySet().stream()
                .map(entry -> new SalesTrendPointResponse(entry.getKey(), entry.getValue(), currency(snapshot, query)))
                .toList();
    }

    public List<SalesByDocumentTypeResponse> salesByDocumentType(CommercialDashboardSnapshot snapshot, CommercialDashboardQuery query) {
        Map<CommercialDashboardDocumentType, BigDecimal> amounts = new LinkedHashMap<>();
        for (CommercialDashboardDocumentType type : CommercialDashboardDocumentType.values()) {
            amounts.put(type, BigDecimal.ZERO);
        }
        for (CommercialDashboardDocument document : snapshot.documents()) {
            amounts.computeIfPresent(document.type(), (ignored, amount) -> amount.add(document.totalAmount()));
        }
        return amounts.entrySet().stream()
                .map(entry -> new SalesByDocumentTypeResponse(entry.getKey().label(), entry.getValue(), currency(snapshot, query)))
                .toList();
    }

    private List<CommercialDashboardDocument> overdueInvoices(List<CommercialDashboardDocument> invoices) {
        return invoices.stream().filter(invoice -> unpaid(invoice) && overdueDays(invoice) > 0).toList();
    }

    private boolean unpaid(CommercialDashboardDocument invoice) {
        return invoice.open() || invoice.openAmount().signum() > 0;
    }

    private long overdueDays(CommercialDashboardDocument invoice) {
        if (invoice.dueDate() == null) {
            return 0;
        }
        long days = ChronoUnit.DAYS.between(invoice.dueDate(), LocalDate.now(clock));
        return Math.max(0, days);
    }

    private String agingBucket(long overdueDays) {
        if (overdueDays <= 0) {
            return "Not due";
        }
        if (overdueDays <= 30) {
            return "1-30 days";
        }
        if (overdueDays <= 60) {
            return "31-60 days";
        }
        if (overdueDays <= 90) {
            return "61-90 days";
        }
        return "90+ days";
    }

    private BigDecimal sum(List<CommercialDashboardDocument> documents, Function<CommercialDashboardDocument, BigDecimal> amount) {
        return documents.stream().map(amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String currency(CommercialDashboardSnapshot snapshot, CommercialDashboardQuery query) {
        if (query.currency() != null) {
            return query.currency();
        }
        Set<String> currencies = snapshot.documents().stream()
                .map(CommercialDashboardDocument::currency)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (currencies.size() == 1) {
            return currencies.iterator().next();
        }
        return currencies.isEmpty() ? "" : "MIXED";
    }

    private static final class ItemAccumulator {
        private final String itemCode;
        private final String itemName;
        private BigDecimal quantity = BigDecimal.ZERO;
        private BigDecimal netAmount = BigDecimal.ZERO;

        private ItemAccumulator(String itemCode, String itemName) {
            this.itemCode = itemCode;
            this.itemName = itemName;
        }

        void add(BigDecimal quantityValue, BigDecimal amountValue) {
            quantity = quantity.add(quantityValue == null ? BigDecimal.ZERO : quantityValue);
            netAmount = netAmount.add(amountValue == null ? BigDecimal.ZERO : amountValue);
        }

        String itemCode() { return itemCode; }
        String itemName() { return itemName; }
        BigDecimal quantity() { return quantity; }
        BigDecimal netAmount() { return netAmount; }
    }

    private static class CustomerAccumulator {
        private final String businessPartnerCode;
        private final String businessPartnerName;
        private BigDecimal amount = BigDecimal.ZERO;

        private CustomerAccumulator(String businessPartnerCode, String businessPartnerName) {
            this.businessPartnerCode = businessPartnerCode;
            this.businessPartnerName = businessPartnerName;
        }

        void add(BigDecimal value) {
            amount = amount.add(value == null ? BigDecimal.ZERO : value);
        }

        String businessPartnerCode() { return businessPartnerCode; }
        String businessPartnerName() { return businessPartnerName; }
        BigDecimal amount() { return amount; }
    }

    private static final class UnpaidCustomerAccumulator extends CustomerAccumulator {
        private int invoiceCount;
        private LocalDate oldestDueDate;
        private long maxOverdueDays;

        private UnpaidCustomerAccumulator(String businessPartnerCode, String businessPartnerName) {
            super(businessPartnerCode, businessPartnerName);
        }

        void add(CommercialDashboardDocument invoice, long overdueDays) {
            add(invoice.openAmount());
            invoiceCount++;
            if (invoice.dueDate() != null && (oldestDueDate == null || invoice.dueDate().isBefore(oldestDueDate))) {
                oldestDueDate = invoice.dueDate();
            }
            maxOverdueDays = Math.max(maxOverdueDays, overdueDays);
        }

        int invoiceCount() { return invoiceCount; }
        LocalDate oldestDueDate() { return oldestDueDate; }
        long maxOverdueDays() { return maxOverdueDays; }
        BigDecimal openAmount() { return amount(); }
    }
}
