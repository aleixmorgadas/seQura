package dev.aleixmorgadas.orders;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByCreatedAt(LocalDate date);
}
