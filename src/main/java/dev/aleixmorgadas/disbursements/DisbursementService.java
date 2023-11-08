package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.merchants.MerchantRepository;
import dev.aleixmorgadas.orders.Order;
import dev.aleixmorgadas.orders.OrderIngestedEvent;
import dev.aleixmorgadas.orders.OrderPlaced;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Slf4j
@Service
@AllArgsConstructor
public class DisbursementService {
    private final DisbursementRepository disbursementRepository;
    private final DisbursementOrderRepository disbursementOrderRepository;
    private final MerchantRepository merchantRepository;
    private final MinimumMonthlyFeeRepository minimumMonthlyFeeRepository;
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<Disbursement> performDisbursementsOn(String date) {
        var localDate = LocalDate.parse(date, DATE_FORMATTER);
        var disbursements = new ArrayList<Disbursement>();

        var merchants = merchantRepository.findAll();
        if (localDate.getDayOfMonth() == 1) {
            merchants.forEach(merchant -> {
                if (localDate.isBefore(LocalDate.parse(merchant.getLiveOn()))) {
                    return;
                }
                var previousMonthBeginning = localDate.minusMonths(1);
                var previousMonthEnd = localDate.minusMonths(1).with(lastDayOfMonth());
                double sumFees = disbursementRepository.sumFeesByMerchantAndDateBetween(merchant.getReference(), previousMonthBeginning, previousMonthEnd);
                if (sumFees < merchant.getMinimumMonthlyFee()) {
                    minimumMonthlyFeeRepository.save(new MinimumMonthlyFee(
                            DisbursementReference.from(merchant, localDate),
                            merchant.getMinimumMonthlyFee() - sumFees,
                            localDate
                    ));
                }
            });
        }
        merchants.stream().filter(merchant -> merchant.isDisbursementDate(localDate)).forEach(merchant -> {
            var disbursementReference = DisbursementReference.from(merchant, localDate);
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
        log.info("orderIngestedEvent received");
        var orders = event.orders();
        log.info("storing orders as DisbursementOrders");
        orders.forEach(this::storeOrder);

        var earliestOrderDate = orders.stream().map(Order::getCreatedAt).min(LocalDate::compareTo).orElseThrow();
        var latestOrderDate = orders.stream().map(Order::getCreatedAt).max(LocalDate::compareTo).orElseThrow();
        log.info("Performing disbursements from {} until {}", earliestOrderDate, latestOrderDate);
        earliestOrderDate.datesUntil(latestOrderDate.plusDays(2)).forEach(date -> performDisbursementsOn(date.format(DATE_FORMATTER)));
        log.info("Performing disbursements completed");
    }

    @Async
    @EventListener
    public void onOrderPlaced(OrderPlaced orderPlaced) {
        var order = orderPlaced.order();
        storeOrder(order);
    }

    private void storeOrder(Order order) {
        var merchant = merchantRepository.findById(order.getMerchantReference())
                .orElseThrow(() -> new RuntimeException("Merchant not found with reference %s".formatted(order.getMerchantReference())));
        var reference = DisbursementReference.from(merchant, order);
        var disbursementOrder = DisbursementOrder.from(order, reference);
        disbursementOrderRepository.save(disbursementOrder);
    }
}
