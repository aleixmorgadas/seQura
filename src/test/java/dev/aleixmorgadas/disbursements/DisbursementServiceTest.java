package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.AbstractIntegrationTest;
import dev.aleixmorgadas.orders.Order;
import dev.aleixmorgadas.orders.OrderIngestedEvent;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

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

    @Test
    @Transactional
    void itShouldPerformADisbursementOnNewOrderIngestedEvent() {
        publisher.publishEvent(
                new OrderIngestedEvent(List.of(
                        new Order(
                                1L,
                                "wintheiser_bernhard",
                                "25.43",
                                LocalDate.parse("2021-05-03")
                        )
                ), LocalDate.parse("2021-05-03")));

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> disbursementRepository.count() == 1);

        var disbursement = disbursementRepository.findAll().get(0);
        assertThat(disbursement.getReference()).isEqualTo("wintheiser_bernhard-20210503");
        assertThat(disbursement.getMerchant()).isEqualTo("wintheiser_bernhard");
        assertThat(disbursement.getDate()).isEqualTo(LocalDate.parse("2021-05-03"));
        assertThat(disbursement.getAmount()).isEqualTo(25.17);
        assertThat(disbursement.getFees()).isEqualTo(0.26);
        assertThat(disbursement.getOrders()).hasSize(1);
    }
}