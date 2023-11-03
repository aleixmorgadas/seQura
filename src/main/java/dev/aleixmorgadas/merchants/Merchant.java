package dev.aleixmorgadas.merchants;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Builder
@Entity(name = "merchants")
@NoArgsConstructor
@AllArgsConstructor
public class Merchant {
    @Id
    String reference;
    String email;
    String liveOn;
    String disbursementFrequency;
    double minimumMonthlyFee;

    public LocalDate nextDisbursementDate(LocalDate date) {
        if ("DAILY".equals(disbursementFrequency)) {
            return date;
        }
        var liveOnDate = LocalDate.parse(liveOn);
        if (liveOnDate.getDayOfWeek() == date.getDayOfWeek()) {
            return date;
        }

        return date.with(TemporalAdjusters.next(liveOnDate.getDayOfWeek()));
    }
}
