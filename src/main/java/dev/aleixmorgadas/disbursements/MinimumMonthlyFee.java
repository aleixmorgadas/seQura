package dev.aleixmorgadas.disbursements;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "minimum_monthly_fee")
public class MinimumMonthlyFee {
    @EmbeddedId
    DisbursementReference reference;
    double amount;
    LocalDate month;
}
