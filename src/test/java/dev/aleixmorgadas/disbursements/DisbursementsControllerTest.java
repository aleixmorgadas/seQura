package dev.aleixmorgadas.disbursements;

import dev.aleixmorgadas.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DisbursementsControllerTest extends AbstractIntegrationTest {

    @Test
    void performDisbursements() throws Exception {
        mockMvc.perform(post("/disbursements"))
                .andExpect(status().isOk());
    }
}
