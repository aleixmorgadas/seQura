package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.AbstractIntegrationTest;
import dev.aleixmorgadas.orders.Order;
import dev.aleixmorgadas.orders.OrderIngestedEvent;
import dev.aleixmorgadas.orders.OrderPlaced;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class DisbursementServiceTest extends AbstractIntegrationTest {
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private DisbursementRepository disbursementRepository;
    @Autowired
    private DisbursementOrderRepository disbursementOrderRepository;

    @AfterEach @AfterTransaction
    void cleanUp() {
        disbursementOrderRepository.deleteAll();
        disbursementRepository.deleteAll();
    }

    @Test @Transactional
    public void itShouldPerformADisbursementOnNewOrderIngestedEvent() {
        publisher.publishEvent(
                new OrderIngestedEvent(List.of(
                        new Order(
                                1L,
                                "wintheiser_bernhard",
                                "50.00",
                                LocalDate.parse("2021-05-03")
                        )
                ), LocalDate.parse("2021-05-03")));

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> disbursementRepository.count() == 1);

        var disbursement = disbursementRepository.findAll().get(0);
        assertThat(disbursement.getReference()).isEqualTo("wintheiser_bernhard-20210503");
        assertThat(disbursement.getMerchant()).isEqualTo("wintheiser_bernhard");
        assertThat(disbursement.getDate()).isEqualTo(LocalDate.parse("2021-05-03"));
        assertThat(disbursement.getAmount()).isEqualTo(49.5);
        assertThat(disbursement.getFees()).isEqualTo(0.5);
        assertThat(disbursement.getOrders()).hasSize(1);
    }

    @Test
    void onOrderPlacedTest() {
        publisher.publishEvent(new OrderPlaced(
                new Order(
                        1L,
                        "wintheiser_bernhard",
                        "25.00",
                        LocalDate.parse("2021-05-03")
                ),
                LocalDate.parse("2021-05-03")
        ));

        await()
                .atMost(1, TimeUnit.SECONDS)
                .until(() -> disbursementOrderRepository.count() == 1);
    }
}