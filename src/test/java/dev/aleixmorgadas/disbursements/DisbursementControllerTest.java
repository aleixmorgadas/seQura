package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.AbstractIntegrationTest;
import dev.aleixmorgadas.merchants.Merchant;
import dev.aleixmorgadas.merchants.MerchantRepository;
import dev.aleixmorgadas.orders.Order;
import dev.aleixmorgadas.orders.OrderRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DisbursementControllerTest extends AbstractIntegrationTest {
    @Autowired
    private DisbursementRepository disbursementRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private OrderRepository orderRepository;

    @BeforeAll
    void setup() {
        merchantRepository.save(new Merchant(
                "wintheiser_bernhard",
                "info@wintheiser-bernhard.com",
                "2022-10-07",
                "DAILY",
                15.0
        ));
        orderRepository.save(new Order(
                null,
                "wintheiser_bernhard",
                "25.43",
                "2022-10-07"
        ));
    }

    @AfterAll
    void tearDown() {
        orderRepository.deleteAll();
        merchantRepository.deleteAll();
        disbursementRepository.deleteAll();
    }

    @Test
    void performDisbursements() throws Exception {
        mockMvc.perform(post(DisbursementController.URI + "/2022-10-08"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].reference").value("wintheiser_bernhard-20221008"))
                .andExpect(jsonPath("$[0].merchant").value("wintheiser_bernhard"))
                .andExpect(jsonPath("$[0].amount").value(25.17))
                .andExpect(jsonPath("$[0].fees").value(0.26))
                .andExpect(jsonPath("$[0].date").value("2022-10-08"))
                .andExpect(jsonPath("$[0].settled").value(true))
                .andExpect(jsonPath("$[0].ordersAmount").value(1));

        var disbursements = disbursementRepository.findById("wintheiser_bernhard-20221008")
                .orElseThrow(() -> new RuntimeException("Disbursement not found"));

        assertThat(disbursements.getMerchant()).isEqualTo("wintheiser_bernhard");
        assertThat(disbursements.getAmount()).isEqualTo(25.17);
        assertThat(disbursements.getFees()).isEqualTo(0.26);
        assertThat(disbursements.getDate()).isEqualTo("2022-10-08");
        assertThat(disbursements.isSettled()).isTrue();
        assertThat(disbursements.getOrders()).hasSize(1);
        assertThat(disbursements.getOrders().get(0).getAmount()).isEqualTo(25.43);
        assertThat(disbursements.getOrders().get(0).getCommission()).isEqualTo(0.26);
        assertThat(disbursements.getOrders().get(0).getCreatedAt()).isEqualTo("2022-10-07");
    }
}
