package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.merchants.Merchant;
import dev.aleixmorgadas.orders.Order;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DisbursementReference {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String reference;

    static DisbursementReference from(Merchant merchant, LocalDate localDate) {
        return new DisbursementReference(
                String.format("%s-%s", merchant.getReference(), localDate.format(DATE_FORMATTER).replace("-", "")));
    }

    static DisbursementReference from(Merchant merchant, Order order) {
        if (!merchant.getReference().equals(order.getMerchantReference())) {
            throw new IllegalArgumentException("Merchant and order must be from the same merchant");
        }
        return new DisbursementReference(
                String.format("%s-%s", merchant.getReference(),
                        merchant.nextDisbursementDate(order.getCreatedAt()).format(DATE_FORMATTER).replace("-", "")));
    }
}
