package dev.aleixmorgadas.orders;

import dev.aleixmorgadas.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerTest extends AbstractIntegrationTest {

    @Test
    void itIngestsOrders() throws Exception {
        mockMvc.perform(multipart("/orders")
            .file("orders", "csv/orders.csv".getBytes())
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());
    }
}
