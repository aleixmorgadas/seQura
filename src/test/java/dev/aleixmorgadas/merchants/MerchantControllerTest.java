package dev.aleixmorgadas.merchants;

import dev.aleixmorgadas.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MerchantControllerTest extends AbstractIntegrationTest {
    @Autowired
    private MerchantRepository merchantRepository;

    @Test
    void itIngestsMerchants() throws Exception {
        var merchants = readCsv("csv/merchants.csv");
        var file = new MockMultipartFile("file", "merchants.csv", "text/csv", merchants.getBytes());

        mockMvc.perform(multipart("/merchants")
            .file(file)
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());

        assertThat(merchantRepository.count()).isEqualTo(50);
    }
}
