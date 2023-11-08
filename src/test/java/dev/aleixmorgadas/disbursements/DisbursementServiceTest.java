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
import org.springframework.transaction.annotation.Propagation;
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
    @Autowired
    private MinimumMonthlyFeeRepository minimumMonthlyFeeRepository;

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
        minimumMonthlyFeeRepository.deleteAll();
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

    @Test
    @Transactional
    void calculatesMinimumCommissionOnTheFirstDayOfTheMonthWhenNoOrdersTookPlace() {
        var merchant = new Merchant("not_much_traffic", "notraffic@example.com", "2023-10-25", "DAILY", 25.0);
        merchantRepository.save(merchant);

        var firstMonthDay = "2023-11-01";
        disbursementService.performDisbursementsOn(firstMonthDay);

        var reference = DisbursementReference.from(merchant, LocalDate.parse(firstMonthDay));
        var minimumMonthlyFee = minimumMonthlyFeeRepository.findById(reference)
                .orElseThrow(() -> new IllegalArgumentException("no minimumMonthlyFee found"));
        assertThat(minimumMonthlyFee.amount).isEqualTo(25.0);
        assertThat(minimumMonthlyFee.month).isEqualTo(LocalDate.parse(firstMonthDay));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void calculatesTheMinimumFeeOfTheMonthWhenTheMerchantHadOrdersButDidNotReachTheMinimum() {
        var merchant = new Merchant("not_much_traffic", "notraffic@example.com", "2023-10-25", "DAILY", 25.0);
        merchantRepository.saveAndFlush(merchant);
        disbursementService.onOrderPlaced(new OrderPlaced(
                new Order(1L, "not_much_traffic", "25", LocalDate.parse("2023-10-28")),
                LocalDate.parse("2023-10-28")
        ));
        disbursementService.performDisbursementsOn("2023-10-29");

        var firstMonthDay = "2023-11-01";
        disbursementService.performDisbursementsOn(firstMonthDay);

        var reference = DisbursementReference.from(merchant, LocalDate.parse(firstMonthDay));
        var minimumMonthlyFee = minimumMonthlyFeeRepository.findById(reference)
                .orElseThrow(() -> new IllegalArgumentException("no minimumMonthlyFee found"));
        assertThat(minimumMonthlyFee.amount).isEqualTo(24.75);
        assertThat(minimumMonthlyFee.month).isEqualTo(LocalDate.parse(firstMonthDay));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void noMinimumMonthlyFeeWhenTheMerchantAlreadyPaidTheMinimumWithTheOrdersFees() {
        var monthlyFee = 2.0;
        var merchant = new Merchant("much_a_lot_of_traffic", "notraffic@example.com", "2023-10-25", "DAILY", monthlyFee);
        merchantRepository.saveAndFlush(merchant);
        disbursementService.onOrderPlaced(new OrderPlaced(
                new Order(1L, "much_a_lot_of_traffic", "2500", LocalDate.parse("2023-10-28")),
                LocalDate.parse("2023-10-28")
        ));
        disbursementService.performDisbursementsOn("2023-10-29");

        var firstMonthDay = "2023-11-01";
        disbursementService.performDisbursementsOn(firstMonthDay);

        var reference = DisbursementReference.from(merchant, LocalDate.parse(firstMonthDay));
        assertThat(minimumMonthlyFeeRepository.findById(reference)).isEmpty();
    }

    @Test
    @Transactional
    void shouldNotApplyTheMinimumMonthlyFeeBeforeTheMerchantSignedUp() {
        var merchant = new Merchant("new_merchant", "new@example.com", "2023-10-05", "DAILY", 25);
        merchantRepository.save(merchant);

        var firstDateOfAPreviousMonth = "2023-09-01";
        disbursementService.performDisbursementsOn(firstDateOfAPreviousMonth);

        var reference = DisbursementReference.from(merchant, LocalDate.parse(firstDateOfAPreviousMonth));
        assertThat(minimumMonthlyFeeRepository.findById(reference)).isEmpty();
    }
}