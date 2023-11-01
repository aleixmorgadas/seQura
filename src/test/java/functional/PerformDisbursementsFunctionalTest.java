package functional;

import dev.aleixmorgadas.AbstractIntegrationTest;
import dev.aleixmorgadas.merchants.MerchantRepository;
import dev.aleixmorgadas.orders.OrderRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PerformDisbursementsFunctionalTest extends AbstractIntegrationTest {
    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeAll
    public void setup() throws Exception {
        var merchants = readCsv("csv/merchants.csv");
        var merchantsRequest = new MockMultipartFile("file", "merchants.csv", "text/csv", merchants.getBytes());

        mockMvc.perform(multipart("/merchants")
                        .file(merchantsRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

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
}
