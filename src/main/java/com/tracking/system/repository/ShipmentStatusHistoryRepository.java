package com.tracking.system.repository;

import com.tracking.system.entity.ShipmentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentStatusHistoryRepository extends JpaRepository<ShipmentStatusHistory, Long> {
    List<ShipmentStatusHistory> findByShipmentIdOrderByCreatedAtAsc(Long shipmentId);
    List<ShipmentStatusHistory> findByShipmentTrackingCodeOrderByCreatedAtAsc(String trackingCode);
}
