package dev.aleixmorgadas.orders;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final ApplicationEventPublisher publisher;

    public void saveAll(List<Order> orders) {
        repository.saveAll(orders);
        publisher.publishEvent(new OrderIngestedEvent(
                orders,
                LocalDate.now()
        ));
    }

    public void save(Order order) {
        repository.save(order);
        publisher.publishEvent(new OrderPlaced(
                order,
                LocalDate.now()
        ));
    }
}
