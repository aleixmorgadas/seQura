package dev.aleixmorgadas.disbursements;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DisbursementOrderRepository extends JpaRepository<DisbursementOrder, Long> {
    List<DisbursementOrder> findByCreatedAt(LocalDate localDate);
}
