package dev.aleixmorgadas.orders;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@Entity(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_seq")
    @SequenceGenerator(name = "orders_seq", sequenceName = "orders_seq", allocationSize = 1)
    Long id;
    String merchantReference;
    String amount;
    LocalDate createdAt;
}
