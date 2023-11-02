package dev.aleixmorgadas.orders;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository repository;

    public void saveAll(List<Order> orders) {
        repository.saveAll(orders);
    }
}
