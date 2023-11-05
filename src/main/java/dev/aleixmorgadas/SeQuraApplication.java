package dev.aleixmorgadas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableRetry
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
public class SeQuraApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeQuraApplication.class, args);
    }
}