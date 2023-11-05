package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.merchants.Merchant;
import dev.aleixmorgadas.merchants.MerchantRepository;
import dev.aleixmorgadas.orders.OrderIngestedEvent;
import dev.aleixmorgadas.orders.OrderPlaced;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class DisbursementService {
    private final DisbursementRepository disbursementRepository;
    private final DisbursementOrderRepository disbursementOrderRepository;
    private final MerchantRepository merchantRepository;
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<Disbursement> performDisbursementsOn(String date) {
        var localDate = LocalDate.parse(date, DATE_FORMATTER);
        var disbursements = new ArrayList<Disbursement>();

        var dailyMerchants = merchantRepository.findAllByDisbursementFrequency("DAILY");
        dailyMerchants.forEach(merchant -> {
            var disbursementReference = generateDisbursementReference(merchant, localDate);
            var disbursement = disbursementRepository.findById(disbursementReference).orElseGet(() -> {
                var orders = disbursementOrderRepository.findByReference(disbursementReference);
                var d = Disbursement.from(disbursementReference, merchant.getReference(), localDate);
                d.addOrders(orders);
                disbursementRepository.save(d);
                return d;
            });
            disbursements.add(disbursement);
        });

        var weeklyMerchants = merchantRepository.findAllByDisbursementFrequency("WEEKLY");
        weeklyMerchants
                .stream()
                .filter(merchant -> merchant.isDisbursementDate(localDate))
                .forEach(merchant -> {
                    var disbursementReference = generateDisbursementReference(merchant, localDate);
                    var disbursement = disbursementRepository.findById(disbursementReference).orElseGet(() -> {
                        var orders = disbursementOrderRepository.findByReference(disbursementReference);
                        var d = Disbursement.from(disbursementReference, merchant.getReference(), localDate);
                        d.addOrders(orders);
                        disbursementRepository.save(d);
                        return d;
                    });
                    disbursements.add(disbursement);
                });
        return disbursements;
    }

    @Async
    @EventListener
    void onOrderIngested(OrderIngestedEvent event) {
        var orders = event.orders();
        var disbursements = new HashMap<String, Disbursement>();
        orders.forEach(order -> {
            var merchant = merchantRepository.findById(order.getMerchantReference())
                    .orElseThrow(() -> new RuntimeException("Merchant not found"));
            String reference = generateDisbursementReference(merchant, order.getCreatedAt());
            var disbursement = disbursements.get(reference);
            if (disbursement == null) {
                var nextDisbursementDate = merchant.nextDisbursementDate(order.getCreatedAt());
                disbursement = disbursementRepository.findById(reference)
                        .orElseGet(() -> Disbursement.from(reference, merchant.getReference(), nextDisbursementDate));
                disbursements.put(reference, disbursement);
            }
            disbursement.addOrder(order);
        });
        disbursementRepository.saveAll(disbursements.values());
        disbursements.clear();
    }

    @Async
    @EventListener
    public void onOrderPlaced(OrderPlaced orderPlaced) {
        var order = orderPlaced.order();
        var merchant = merchantRepository.findById(order.getMerchantReference())
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
        var reference = generateDisbursementReference(merchant, merchant.nextDisbursementDate(order.getCreatedAt()));
        var disbursementOrder = DisbursementOrder.from(order, reference);
        disbursementOrderRepository.save(disbursementOrder);
    }

    private String generateDisbursementReference(Merchant merchant, LocalDate date) {
        return merchant.getReference() + "-" + date.format(DATE_FORMATTER).replace("-", "");
    }
}
