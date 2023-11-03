package dev.aleixmorgadas.disbursements;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DisbursementOrderRepository extends JpaRepository<DisbursementOrder, Long> {
}
