package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.AbstractIntegrationTest;
import dev.aleixmorgadas.merchants.Merchant;
import dev.aleixmorgadas.merchants.MerchantRepository;
import dev.aleixmorgadas.orders.Order;
import dev.aleixmorgadas.orders.OrderIngestedEvent;
import dev.aleixmorgadas.orders.OrderPlaced;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DisbursementServiceTest extends AbstractIntegrationTest {
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private DisbursementRepository disbursementRepository;
    @Autowired
    private DisbursementOrderRepository disbursementOrderRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private DisbursementService disbursementService;

    private final String DAILY_MERCHANT = "daily_merchant";
    private final String WEEKLY_MERCHANT = "weekly_merchant";

    @BeforeEach
    @BeforeTransaction
    void setup() {
        merchantRepository.save(new Merchant(DAILY_MERCHANT, "hi@example.com", "2023-11-01", "DAILY", 25.5));
        merchantRepository.save(new Merchant(WEEKLY_MERCHANT, "merchant2@example.com", "2023-10-25", "WEEKLY", 25.5)); // Wednesday
    }

    @AfterEach
    @AfterTransaction
    void cleanUp() {
        merchantRepository.deleteAll();
        disbursementOrderRepository.deleteAll();
        disbursementRepository.deleteAll();
    }

    @Test
    @Transactional
    public void itShouldPerformADisbursementOnNewOrderIngestedEvent() {
        publisher.publishEvent(new OrderIngestedEvent(List.of(new Order(1L, DAILY_MERCHANT, "50.00", LocalDate.parse("2023-11-03"))), LocalDate.parse("2023-11-03")));

        await().atMost(1, TimeUnit.SECONDS).until(() -> disbursementRepository.count() == 2);

        var disbursement = disbursementRepository.findByMerchantAndDate(DAILY_MERCHANT, LocalDate.parse("2023-11-04"))
                        .orElseThrow(() -> new AssertionError("Disbursement not found"));
        assertThat(disbursement.getReference().reference).isEqualTo(DAILY_MERCHANT + "-20231104");
        assertThat(disbursement.getMerchant()).isEqualTo(DAILY_MERCHANT);
        assertThat(disbursement.getDate()).isEqualTo(LocalDate.parse("2023-11-04"));
        assertThat(disbursement.getAmount()).isEqualTo(49.5);
        assertThat(disbursement.getFees()).isEqualTo(0.5);
        assertThat(disbursement.getOrders()).hasSize(1);
    }

    @Test
    void onOrderPlacedTest() {
        publisher.publishEvent(new OrderPlaced(new Order(1L, DAILY_MERCHANT, "25.00", LocalDate.parse("2023-11-03")), LocalDate.parse("2023-11-03")));

        await().atMost(1, TimeUnit.SECONDS).until(() -> disbursementOrderRepository.count() == 1);
    }

    @Test
    @Transactional
    void performADisbursementWithWeeklyMerchantsInvolved() {
        var disbursementOrderOnTuesday = new DisbursementOrder(1L, WEEKLY_MERCHANT, 50.00, 0.5, LocalDate.parse("2023-11-03"), new DisbursementReference(WEEKLY_MERCHANT + "-20231108"));
        var disbursementOrderOnWednesday = new DisbursementOrder(2L, WEEKLY_MERCHANT, 50.00, 0.5, LocalDate.parse("2023-11-08"), new DisbursementReference(WEEKLY_MERCHANT + "-20231115"));
        disbursementOrderRepository.saveAll(List.of(disbursementOrderOnTuesday, disbursementOrderOnWednesday));

        var disbursements = disbursementService.performDisbursementsOn("2023-11-08");

        assertThat(disbursements).hasSize(2);
        assertThat(disbursements.get(1).getOrders().get(0)).isEqualTo(disbursementOrderOnTuesday);
    }

    @Test
    @Transactional
    void performDisbursementsIsAnIdempotentOperation() {
        var disbursementOrderOnTuesday = new DisbursementOrder(1L, WEEKLY_MERCHANT, 50.00, 0.5, LocalDate.parse("2023-11-03"), new DisbursementReference(WEEKLY_MERCHANT + "-20231108"));
        disbursementOrderRepository.save(disbursementOrderOnTuesday);

        var disbursements = disbursementService.performDisbursementsOn("2023-11-08");

        assertThat(disbursements).hasSize(2);
        assertThat(disbursements.get(1).getOrders().get(0)).isEqualTo(disbursementOrderOnTuesday);

        var idempotentDisbursements = disbursementService.performDisbursementsOn("2023-11-08");
        assertThat(idempotentDisbursements.get(1).getOrders()).hasSize(1);
    }
}