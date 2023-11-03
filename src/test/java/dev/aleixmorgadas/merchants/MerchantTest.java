package dev.aleixmorgadas.merchants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class MerchantTest {

    @Test
    void onADailyDisbursementFrequencyTheDisbursementDateIsTheSame() {
        var dailyDisbursementMerchant = Merchant.builder()
                .reference("MERCHANT-1")
                .email("merchant@example.com")
                .liveOn("2021-01-01")
                .minimumMonthlyFee(25)
                .disbursementFrequency("DAILY")
                .build();

        assertThat(dailyDisbursementMerchant.nextDisbursementDate(LocalDate.parse("2023-11-03")))
                .isEqualTo(LocalDate.parse("2023-11-03"));
    }

    @ParameterizedTest
    @MethodSource("disbursementDates")
    void onAWeeklyDisbursementFrequencyTheDisbursementDateTheSameDateOrTheNextWorkWeekAsLiveOn(String liveOn, LocalDate input, LocalDate expected) {
        var dailyDisbursementMerchant = Merchant.builder()
                .reference("MERCHANT-1")
                .email("merchant@example.com")
                .liveOn(liveOn)
                .minimumMonthlyFee(25)
                .disbursementFrequency("WEEKLY")
                .build();

        assertThat(dailyDisbursementMerchant.nextDisbursementDate(input))
                .isEqualTo(expected);
    }

    private static Stream<Arguments> disbursementDates() {
        return Stream.of(
                Arguments.of("2023-11-01", LocalDate.parse("2023-11-03"), LocalDate.parse("2023-11-08")),
                Arguments.of("2023-11-01", LocalDate.parse("2023-11-08"), LocalDate.parse("2023-11-08"))
        );
    }
}