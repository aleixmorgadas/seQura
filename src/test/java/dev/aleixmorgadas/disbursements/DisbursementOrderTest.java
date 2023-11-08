package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.merchants.Merchant;
import dev.aleixmorgadas.orders.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DisbursementOrderTest {

    @ParameterizedTest
    @MethodSource("disbursements")
    void disbursementFees(String amount, double commission) {
        var disbursementReference = DisbursementReference.from(
                new Merchant("MERCHANT-1", "email@example.com", "2023-11-01", "DAILY", 25.0),
                LocalDate.parse("2023-11-08"));
        var order = new Order(1L, "MERCHANT-1", amount, LocalDate.parse("2023-11-08"));
        var disbursementOrder = DisbursementOrder.from(order, disbursementReference);
        assertThat(disbursementOrder.getAmount()).isEqualTo(Double.parseDouble(amount));
        assertThat(disbursementOrder.getCommission()).isEqualTo(commission);
    }

    private static Stream<Arguments> disbursements() {
        return Stream.of(
                Arguments.of("45", 0.45),
                Arguments.of("50", 0.5),
                Arguments.of("51", 0.49),
                Arguments.of("200", 1.9),
                Arguments.of("300", 2.85),
                Arguments.of("301", 2.56),
                Arguments.of("500", 4.25)
        );
    }
}