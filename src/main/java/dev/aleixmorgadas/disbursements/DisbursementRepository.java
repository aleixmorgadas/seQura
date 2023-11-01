package dev.aleixmorgadas.disbursements;

import org.springframework.data.repository.CrudRepository;

public interface DisbursementRepository extends CrudRepository<Disbursement, String> {
}
