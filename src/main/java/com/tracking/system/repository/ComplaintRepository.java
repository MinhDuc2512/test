package com.tracking.system.repository;

import com.tracking.system.entity.Complaint;
import com.tracking.system.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByShipmentTrackingCode(String trackingCode);
    List<Complaint> findByCreatedByUserId(Long userId);
    long countByStatus(ComplaintStatus status);
}

