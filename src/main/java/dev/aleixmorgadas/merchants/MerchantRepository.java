package dev.aleixmorgadas.merchants;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantRepository extends JpaRepository<Merchant, String> {
    List<Merchant> findAllByDisbursementFrequency(String frequency);
}
