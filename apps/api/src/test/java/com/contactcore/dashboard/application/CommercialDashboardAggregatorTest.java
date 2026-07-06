// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class CommercialDashboardAggregatorTest {
    private final CommercialDashboardAggregator aggregator = new CommercialDashboardAggregator(
            Clock.fixed(Instant.parse("2026-07-06T00:00:00Z"), ZoneOffset.UTC)
    );
    private final CommercialDashboardQuery query = new CommercialDashboardQuery(
            new CommercialDashboardDateRange(LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31")),
            "CHF",
            10,
            CommercialDashboardGroupBy.MONTH
    );

    @Test
    void aggregatesSummaryAndRankedWidgets() {
        CommercialDashboardSnapshot snapshot = new CommercialDashboardSnapshot(List.of(
                invoice("1", "C1", "Acme AG", "2026-01-10", "2026-02-10", "1000", "400", true,
                        line("I1", "Notebook", "2", "700"), line("I2", "Mouse", "3", "300")),
                invoice("2", "C2", "Beta AG", "2026-02-12", "2026-03-12", "500", "500", false,
                        line("I1", "Notebook", "1", "500"))
        ));

        assertThat(aggregator.summary(snapshot, query).totalSales()).isEqualByComparingTo("1500");
        assertThat(aggregator.summary(snapshot, query).openInvoiceAmount()).isEqualByComparingTo("600");
        assertThat(aggregator.summary(snapshot, query).unpaidInvoiceCount()).isEqualTo(1);
        assertThat(aggregator.topSellingItems(snapshot, query).getFirst().itemCode()).isEqualTo("I1");
        assertThat(aggregator.topCustomers(snapshot, query).getFirst().businessPartnerCode()).isEqualTo("C1");
    }

    @Test
    void aggregatesUnpaidInvoicesAgingAndMonthlyTrend() {
        CommercialDashboardSnapshot snapshot = new CommercialDashboardSnapshot(List.of(
                invoice("1", "C1", "Acme AG", "2026-01-10", "2026-07-01", "1000", "400", true, line("I1", "Notebook", "2", "700")),
                invoice("2", "C1", "Acme AG", "2026-02-12", "2026-04-01", "500", "0", true, line("I2", "Mouse", "3", "500"))
        ));

        assertThat(aggregator.unpaidInvoiceCustomers(snapshot, query).getFirst().invoiceCount()).isEqualTo(2);
        assertThat(aggregator.unpaidInvoiceCustomers(snapshot, query).getFirst().maxOverdueDays()).isEqualTo(96);
        assertThat(aggregator.invoiceAging(snapshot, query))
                .anySatisfy(bucket -> {
                    assertThat(bucket.bucket()).isEqualTo("90+ days");
                    assertThat(bucket.openAmount()).isEqualByComparingTo("500");
                });
        assertThat(aggregator.salesTrend(snapshot, query))
                .anySatisfy(point -> {
                    assertThat(point.period()).isEqualTo("2026-01");
                    assertThat(point.netAmount()).isEqualByComparingTo("1000");
                });
    }

    private static CommercialDashboardDocument invoice(String id,
                                                       String customerCode,
                                                       String customerName,
                                                       String date,
                                                       String dueDate,
                                                       String total,
                                                       String paid,
                                                       boolean open,
                                                       CommercialDashboardLine... lines) {
        return new CommercialDashboardDocument(
                CommercialDashboardDocumentType.INVOICE,
                id,
                customerCode,
                customerName,
                LocalDate.parse(date),
                LocalDate.parse(dueDate),
                new BigDecimal(total),
                new BigDecimal(paid),
                "CHF",
                open,
                List.of(lines)
        );
    }

    private static CommercialDashboardLine line(String code, String name, String quantity, String amount) {
        return new CommercialDashboardLine(code, name, new BigDecimal(quantity), new BigDecimal(amount));
    }
}
