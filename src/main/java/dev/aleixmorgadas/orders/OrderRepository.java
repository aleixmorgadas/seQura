package dev.aleixmorgadas.orders;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByCreatedAt(String date);
}
