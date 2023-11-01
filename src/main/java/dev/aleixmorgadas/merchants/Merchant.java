package dev.aleixmorgadas.merchants;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

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
    float minimumMonthlyFee;
}
