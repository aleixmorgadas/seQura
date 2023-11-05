package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.orders.Order;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Getter
@EqualsAndHashCode
@Entity(name = "disbursements_orders")
@NoArgsConstructor
@AllArgsConstructor
public class DisbursementOrder {
    @Id
    private long id;
    private String merchant;
    private double amount;
    private double commission;
    private LocalDate createdAt;
    private String reference;

    public static DisbursementOrder from(Order order, String reference) {
        var amount = Double.parseDouble(order.getAmount());
        var commission = 0.0;
        if (amount <= 50) {
            commission = amount * 0.01;
        }
        else if (amount <= 300) {
            commission = amount * 0.0095;
        }
        else {
            commission = amount * 0.0085;
        }
        var roundedCommission = BigDecimal.valueOf(commission)
                .setScale(2, RoundingMode.UP)
                .doubleValue();
        return new DisbursementOrder(
                order.getId(),
                order.getMerchantReference(),
                Double.parseDouble(order.getAmount()),
                roundedCommission,
                order.getCreatedAt(),
                reference);
    }
}
