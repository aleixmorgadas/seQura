package dev.aleixmorgadas.disbursements;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class DisbursementCronJobTest {
    private final CronExpression cronExpression = CronExpression.parse(DisbursementCronJob.CRON_EXPRESSION);

    @Test
    void cronJobIsScheduledFor0005UTCDaily() {
        var currentHour = ChronoLocalDateTime.from(LocalDateTime.of(2023, 11, 5, 13, 4));
        var nextExecution = ChronoLocalDateTime.from(LocalDateTime.of(2023, 11, 6, 0,5));
        assertThat(cronExpression.next(currentHour))
                .isEqualTo(nextExecution);
    }
}
