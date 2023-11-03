package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.orders.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity(name = "disbursements")
@AllArgsConstructor
@NoArgsConstructor
public class Disbursement {
    @Id
    private String reference;
    private String merchant;
    private double amount;
    private double fees;
    private LocalDate date;
    @OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true)
    @JoinColumn(name = "reference", referencedColumnName = "reference")
    private List<DisbursementOrder> orders;
    @Version
    private long version;

    public static Disbursement from(String reference, String merchant, LocalDate date) {
        return new Disbursement(reference, merchant, 0, 0, date, new ArrayList<>(), 0);
    }

    public void addOrder(Order order) {
        var disbursementOrder = DisbursementOrder.from(order);
        this.orders.add(disbursementOrder);
        this.amount += BigDecimal.valueOf(disbursementOrder.getAmount() - disbursementOrder.getCommission())
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        this.fees += disbursementOrder.getCommission();
    }
}
