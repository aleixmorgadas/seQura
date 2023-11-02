package dev.aleixmorgadas.disbursements;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DisbursementRepository extends JpaRepository<Disbursement, String> {
}
