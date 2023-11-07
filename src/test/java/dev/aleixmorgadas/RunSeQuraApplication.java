package dev.aleixmorgadas;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@Slf4j
@TestConfiguration(proxyBeanMethods = false)
public class RunSeQuraApplication {
    Network network = Network.newNetwork();

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
        var postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.0"));
        postgres.withNetwork(network);
        postgres.withNetworkAliases("stateful-postgres");
        postgres.setPortBindings(List.of("5555:5432"));
        postgres.withFileSystemBind("./.data/postgres", "/var/lib/postgresql/data");
        postgres.waitingFor(Wait.forSuccessfulCommand("psql -U test -c 'SELECT 1;'"));
        return postgres;
    }

    @Bean
    @ConditionalOnProperty(value = "metabase", havingValue = "true")
    GenericContainer<?> metabaseContainer() {
        var metabase = new GenericContainer<>(DockerImageName.parse("metabase/metabase"));
        metabase.withNetwork(network);
        metabase.setPortBindings(List.of("3000:3000"));
        metabase.withFileSystemBind("./.data/metabase/", "/metabase.db/");
        return metabase;
    }

    public static void main(String[] args) {
        SpringApplication.from(SeQuraApplication::main).with(RunSeQuraApplication.class).run(args);
    }
}