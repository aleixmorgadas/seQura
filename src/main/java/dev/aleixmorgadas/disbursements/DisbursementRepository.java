package dev.aleixmorgadas.disbursements;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DisbursementRepository extends JpaRepository<Disbursement, DisbursementReference> {

    Optional<Disbursement> findByMerchantAndDate(String merchant, LocalDate date);

    List<Disbursement> findByMerchantAndDateBetween(String merchant, LocalDate startAt, LocalDate endAt);
}
