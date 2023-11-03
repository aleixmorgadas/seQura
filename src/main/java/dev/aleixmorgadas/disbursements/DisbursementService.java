package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.orders.OrderIngestedEvent;
import dev.aleixmorgadas.orders.OrderPlaced;
import dev.aleixmorgadas.orders.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class DisbursementService {
    private final DisbursementRepository disbursementRepository;
    private final DisbursementOrderRepository disbursementOrderRepository;
    private final OrderRepository orderRepository;
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<Disbursement> performDisbursementsOn(String date) {
        var localDate = LocalDate.parse(date, DATE_FORMATTER);
        var orders = orderRepository.findByCreatedAt(localDate.minusDays(1));
        var disbursements = new HashMap<String, Disbursement>();
        orders.forEach(order -> {
            var merchant = order.getMerchantReference();
            var disbursement = disbursements.get(merchant);
            if (disbursement == null) {
                String reference = generateDisbursementReference(merchant, date);
                disbursement = disbursementRepository.findById(reference)
                        .orElseGet(() -> Disbursement.from(reference, merchant, LocalDate.parse(date)));
                disbursements.put(merchant, disbursement);
            }
            disbursement.addOrder(order);
        });
        var dis = disbursements.values().stream().toList();
        disbursementRepository.saveAll(dis);
        return dis;
    }

    @Async @EventListener
    void onOrderIngested(OrderIngestedEvent event) {
        var orders = event.orders();
        var disbursements = new HashMap<String, Disbursement>();
        orders.forEach(order -> {
            var merchant = order.getMerchantReference();
            var disbursement = disbursements.get(merchant);
            if (disbursement == null) {
                String reference = generateDisbursementReference(merchant, order.getCreatedAt().format(DATE_FORMATTER));
                disbursement = disbursementRepository.findById(reference)
                        .orElseGet(() -> Disbursement.from(reference, merchant, order.getCreatedAt()));
                disbursements.put(merchant, disbursement);
            }
            disbursement.addOrder(order);
        });
        disbursementRepository.saveAll(disbursements.values());
        disbursements.clear();
    }

    @Async @EventListener
    public void onOrderPlaced(OrderPlaced orderPlaced) {
        var order = orderPlaced.order();
        var disbursementOrder = DisbursementOrder.from(order);
        disbursementOrderRepository.save(disbursementOrder);
    }

    private String generateDisbursementReference(String merchant, String date) {
        return merchant + "-" + date.replace("-", "");
    }
}
