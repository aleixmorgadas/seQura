package dev.aleixmorgadas.disbursements;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DisbursementRepository extends JpaRepository<Disbursement, DisbursementReference> {

    Optional<Disbursement> findByMerchantAndDate(String merchant, LocalDate date);

    @Query(value = "SELECT COALESCE(SUM(d.fees), 0) FROM disbursements d WHERE d.merchant = :merchant AND d.date >= :startAt AND d.date <= :endAt")
    double sumFeesByMerchantAndDateBetween(@Param("merchant") String merchant, @Param("startAt") LocalDate startAt, @Param("endAt") LocalDate endAt);

    @Query("""
            SELECT new dev.aleixmorgadas.disbursements.DisbursementReport(
              DATE_TRUNC('year', d.date),
              COUNT(*),
              SUM(d.amount),
              SUM(d.fees))
            FROM
              disbursements d
            WHERE
              (d.amount <> 0)
            GROUP BY
              DATE_TRUNC('year', d.date)
            ORDER BY
              DATE_TRUNC('year', d.date) ASC
            """)
    List<DisbursementReport> byYearDisbursementReport();
}
