package dev.aleixmorgadas;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {
    protected final ObjectMapper objectMapper = new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    @Autowired
    protected MockMvc mockMvc;

    @ServiceConnection
    static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.0");

    static {
        postgresContainer.start();
    }

    protected String readCsv(String path) throws IOException {
        return IOUtils.toString(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8);
    }

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
