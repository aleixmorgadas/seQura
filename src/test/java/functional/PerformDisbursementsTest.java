package functional;

import dev.aleixmorgadas.AbstractIntegrationTest;
import dev.aleixmorgadas.SeQuraApplication;
import dev.aleixmorgadas.merchants.MerchantRepository;
import dev.aleixmorgadas.orders.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = SeQuraApplication.class)
public class PerformDisbursementsTest extends AbstractIntegrationTest {
    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeAll
    public void setup() throws Exception {
        log.info("PerformDisbursementsTest setup");

        log.info("PerformDisbursementsTest setup - load merchants");
        var merchants = readCsv("csv/merchants.csv");
        var merchantsRequest = new MockMultipartFile("file", "merchants.csv", "text/csv", merchants.getBytes());
        mockMvc.perform(multipart("/merchants")
                        .file(merchantsRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        log.info("PerformDisbursementsTest setup - load orders");
        var orders = readCsv("csv/orders.csv");
        var ordersRequest = new MockMultipartFile("file", "orders.csv", "text/csv", orders.getBytes());
        mockMvc.perform(multipart("/orders")
                        .file(ordersRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @AfterAll
    public void tearDown() {
        merchantRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void foo() {
        assertThat(true).isTrue();
    }
}
