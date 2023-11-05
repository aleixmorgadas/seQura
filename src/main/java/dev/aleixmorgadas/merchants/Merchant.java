package dev.aleixmorgadas.merchants;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Builder
@Getter
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
            return date.plusDays(1);
        }
        var liveOnDate = LocalDate.parse(liveOn);
        if (liveOnDate.getDayOfWeek() == date.getDayOfWeek()) {
            return date.plusDays(7);
        }

        return date.with(TemporalAdjusters.next(liveOnDate.getDayOfWeek()));
    }

    public boolean isDisbursementDate(LocalDate date) {
        if ("DAILY".equals(disbursementFrequency)) {
            return true;
        }
        var liveOnDate = LocalDate.parse(liveOn);
        return liveOnDate.getDayOfWeek() == date.getDayOfWeek();
    }
}
