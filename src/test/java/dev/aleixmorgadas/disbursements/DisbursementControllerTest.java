package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.AbstractIntegrationTest;
import dev.aleixmorgadas.merchants.Merchant;
import dev.aleixmorgadas.merchants.MerchantRepository;
import dev.aleixmorgadas.orders.Order;
import dev.aleixmorgadas.orders.OrderRepository;
import dev.aleixmorgadas.orders.OrderService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private DisbursementService disbursementService;

    @BeforeAll
    void setup() {
        merchantRepository.save(new Merchant(
                "wintheiser_bernhard",
                "info@wintheiser-bernhard.com",
                "2022-10-07",
                "DAILY",
                15.0
        ));
        orderService.save(new Order(
                null,
                "wintheiser_bernhard",
                "25.43",
                LocalDate.parse("2022-10-07")
        ));
    }

    @AfterAll
    void tearDown() {
        orderRepository.deleteAll();
        merchantRepository.deleteAll();
        cleanDisbursements();
    }

    @Test
    @Transactional
    void performDisbursements() throws Exception {
        mockMvc.perform(post(DisbursementController.URI + "/2022-10-08"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].reference").value("wintheiser_bernhard-20221008"))
                .andExpect(jsonPath("$[0].merchant").value("wintheiser_bernhard"))
                .andExpect(jsonPath("$[0].amount").value(25.17))
                .andExpect(jsonPath("$[0].fees").value(0.26))
                .andExpect(jsonPath("$[0].date").value("2022-10-08"))
                .andExpect(jsonPath("$[0].ordersAmount").value(1));

        var disbursements = disbursementRepository.findById(new DisbursementReference("wintheiser_bernhard-20221008"))
                .orElseThrow(() -> new RuntimeException("Disbursement not found"));

        assertThat(disbursements.getMerchant()).isEqualTo("wintheiser_bernhard");
        assertThat(disbursements.getAmount()).isEqualTo(25.17);
        assertThat(disbursements.getFees()).isEqualTo(0.26);
        assertThat(disbursements.getDate()).isEqualTo("2022-10-08");
        assertThat(disbursements.getOrders()).hasSize(1);
        assertThat(disbursements.getOrders().get(0).getAmount()).isEqualTo(25.43);
        assertThat(disbursements.getOrders().get(0).getCommission()).isEqualTo(0.26);
        assertThat(disbursements.getOrders().get(0).getCreatedAt()).isEqualTo("2022-10-07");
    }

    @Test
    void yearReport() throws Exception {
        disbursementService.performDisbursementsOn("2022-10-08");

        mockMvc.perform(get(DisbursementController.URI + "/by-year"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].year").value("2022"))
                .andExpect(jsonPath("$[0].disbursements").value(1))
                .andExpect(jsonPath("$[0].amount").value(25.17))
                .andExpect(jsonPath("$[0].fees").value(0.26));

    }
}
