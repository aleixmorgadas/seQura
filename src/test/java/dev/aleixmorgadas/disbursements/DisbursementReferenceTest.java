package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.merchants.Merchant;
import dev.aleixmorgadas.orders.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class DisbursementReferenceTest {

    @Test
    void disbursementOrderReferenceFromMerchantAndLocalDate() {
        var reference = DisbursementReference.from(
                new Merchant("merchant_ref", "merchant@example.com", "2023-11-03", "WEEKLY", 25.0),
                LocalDate.of(2023, 11, 8)
        );
        assertThat(reference.reference).isEqualTo("merchant_ref-20231108");
    }

    @Test
    void disbursementOrderReferenceFromDailyMerchantAndOrder() {
        var reference = DisbursementReference.from(
                new Merchant("merchant_ref", "merchant@example.com", "2023-11-03", "DAILY", 25.0),
                new Order(1L, "merchant_ref", "50.0", LocalDate.parse("2023-11-08"))
        );
        assertThat(reference.reference).isEqualTo("merchant_ref-20231109");
    }

    @Test
    void disbursementOrderReferenceFromWeeklyMerchantAndOrder() {
        var reference = DisbursementReference.from(
                new Merchant("merchant_ref", "merchant@example.com", "2023-11-03", "WEEKLY", 25.0),
                new Order(1L, "merchant_ref", "50.0", LocalDate.parse("2023-11-08"))
        );
        assertThat(reference.reference).isEqualTo("merchant_ref-20231110");
    }

    @Test
    void throwsExceptionWhenMerchantAndOrderAreNotTheSame() {
        assertThatThrownBy(() -> {
            DisbursementReference.from(
                    new Merchant("merchant_ref", "merchant@example.com", "2023-11-03", "DAILY", 25.0),
                    new Order(1L, "another_merchant", "50.0", LocalDate.parse("2023-11-08"))
            );
        }).isInstanceOf(IllegalArgumentException.class);
    }
}