package dev.aleixmorgadas.disbursements;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface DisbursementRepository extends JpaRepository<Disbursement, DisbursementReference> {

    Optional<Disbursement> findByMerchantAndDate(String merchant, LocalDate date);

    @Query(value = "SELECT COALESCE(SUM(d.fees), 0) FROM disbursements d WHERE d.merchant = :merchant AND d.date >= :startAt AND d.date <= :endAt")
    double sumFeesByMerchantAndDateBetween(@Param("merchant") String merchant, @Param("startAt") LocalDate startAt, @Param("endAt") LocalDate endAt);
}
