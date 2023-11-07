package dev.aleixmorgadas.disbursements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DisbursementReport implements Serializable {
    LocalDate year;
    long disbursements;
    Double amount;
    Double fees;
}
