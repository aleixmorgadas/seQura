package dev.aleixmorgadas.disbursements;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MinimumMonthlyFeeRepository extends JpaRepository<MinimumMonthlyFee, DisbursementReference> {
}
