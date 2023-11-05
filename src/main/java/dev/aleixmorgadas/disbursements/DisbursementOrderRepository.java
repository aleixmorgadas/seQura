package dev.aleixmorgadas.disbursements;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisbursementOrderRepository extends JpaRepository<DisbursementOrder, Long> {
    List<DisbursementOrder> findByReference(DisbursementReference reference);
}
