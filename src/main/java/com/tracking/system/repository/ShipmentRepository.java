package com.tracking.system.repository;

import com.tracking.system.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByTrackingCode(String trackingCode);
    List<Shipment> findByTrackingCodeIn(List<String> trackingCodes);
    boolean existsByTrackingCode(String trackingCode);

    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    // Đếm đơn theo trạng thái
    long countByCurrentStatusCode(String statusCode);

    // Tổng COD theo trạng thái (dùng cho analytics)
    @Query("SELECT COALESCE(SUM(s.codAmount), 0) FROM Shipment s WHERE s.codAmount IS NOT NULL")
    BigDecimal sumTotalCodAmount();

    @Query("SELECT COALESCE(SUM(s.codAmount), 0) FROM Shipment s WHERE s.currentStatusCode NOT IN ('DELIVERED', 'RETURNED') AND s.codAmount IS NOT NULL")
    BigDecimal sumPendingCodAmount();

    // Đếm theo nhóm trạng thái cho dashboard
    @Query("SELECT s.currentStatusCode, COUNT(s) FROM Shipment s GROUP BY s.currentStatusCode")
    List<Object[]> countGroupByStatus();
}
