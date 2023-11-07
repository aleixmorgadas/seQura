package dev.aleixmorgadas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@TestConfiguration(proxyBeanMethods = false)
public class RunSeQuraApplication {

    @Bean
    @ServiceConnection
    @ConditionalOnProperty(value = "stateful", havingValue = "false", matchIfMissing = true)
    PostgreSQLContainer<?> postgresStatelessContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.0"));
    }

    @Bean
    @ServiceConnection
    @ConditionalOnProperty(value = "stateful", havingValue = "true")
    PostgreSQLContainer<?> postgresStatefulContainer() {
        var postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.0"))
                .withReuse(true);
        postgres.setPortBindings(List.of("5555:5432"));
        postgres.withFileSystemBind("./.data/postgres", "/var/lib/postgresql/data");
        return postgres;
    }

    public static void main(String[] args) {
        SpringApplication.from(SeQuraApplication::main).with(RunSeQuraApplication.class).run(args);
    }

}