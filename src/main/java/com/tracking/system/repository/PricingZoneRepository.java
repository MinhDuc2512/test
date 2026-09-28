package com.tracking.system.repository;

import com.tracking.system.entity.PricingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricingZoneRepository extends JpaRepository<PricingZone, Long> {
    Optional<PricingZone> findByFromProvinceAndToProvince(String fromProvince, String toProvince);
}
