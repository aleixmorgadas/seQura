package dev.aleixmorgadas.orders;

import dev.aleixmorgadas.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerTest extends AbstractIntegrationTest {

    @Test
    void itIngestsOrders() throws Exception {
        var orders = readCsv("csv/orders.csv");
        var file = new MockMultipartFile("file", "orders.csv", "text/csv", orders.getBytes());

        mockMvc.perform(multipart("/orders")
            .file(file)
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());
    }
}
