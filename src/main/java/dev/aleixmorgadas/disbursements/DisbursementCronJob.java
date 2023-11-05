package dev.aleixmorgadas.disbursements;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@AllArgsConstructor
public class DisbursementCronJob {
    public static final String CRON_EXPRESSION = "0 5 0 * * *";
    private final DisbursementService disbursementService;

    @Scheduled(cron = CRON_EXPRESSION, zone = "UTC")
    void performDisbursements() {
        disbursementService.performDisbursementsOn(LocalDate.now().format(DisbursementService.DATE_FORMATTER));
    }
}
