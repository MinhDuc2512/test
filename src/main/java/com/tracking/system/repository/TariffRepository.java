package com.tracking.system.repository;

import com.tracking.system.entity.Tariff;
import com.tracking.system.enums.ZoneTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {
    Optional<Tariff> findFirstByServiceTypeIdAndZoneTierAndWeightFromKgLessThanEqualAndWeightToKgGreaterThanEqual(
            Long serviceTypeId, ZoneTier zoneTier, BigDecimal weight1, BigDecimal weight2);
}
