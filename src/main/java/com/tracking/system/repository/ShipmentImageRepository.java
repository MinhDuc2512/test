package com.tracking.system.entity;

import com.tracking.system.entity.ShipmentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentImageRepository extends JpaRepository<ShipmentImage, Long> {
    List<ShipmentImage> findByShipmentTrackingCode(String trackingCode);
    List<ShipmentImage> findByShipmentId(Long shipmentId);
}
