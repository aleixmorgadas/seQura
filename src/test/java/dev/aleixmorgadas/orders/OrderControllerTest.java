package dev.aleixmorgadas.orders;

import dev.aleixmorgadas.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerTest extends AbstractIntegrationTest {
    @Autowired
    private OrderRepository orderRepository;

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
    }

    @Test
    void itIngestsOrders() throws Exception {
        var orders = readCsv("csv/orders-partial.csv");
        var file = new MockMultipartFile("file", "orders.csv", "text/csv", orders.getBytes());

        mockMvc.perform(multipart("/orders")
            .file(file)
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());

        assertThat(orderRepository.count()).isEqualTo(15270);
    }

    @Test
    void newOrder() throws Exception {
        var orderRequest = new OrderController.OrderRequest("merchant", "50.10", LocalDate.now().format(DATE_FORMATTER));
        mockMvc.perform(post(OrderController.URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());

        assertThat(orderRepository.count()).isEqualTo(1);
    }
}
