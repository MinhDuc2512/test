package com.tracking.system.service.impl;

import com.tracking.system.dto.response.DashboardStatsResponse;
import com.tracking.system.enums.ComplaintStatus;
import com.tracking.system.repository.ComplaintRepository;
import com.tracking.system.repository.ShipmentRepository;
import com.tracking.system.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ShipmentRepository shipmentRepository;
    private final ComplaintRepository complaintRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        long total = shipmentRepository.count();

        // Đếm theo khoảng thời gian
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.toLocalDate().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();

        long today = shipmentRepository.countByCreatedAtBetween(startOfDay, now);
        long thisWeek = shipmentRepository.countByCreatedAtBetween(startOfWeek, now);
        long thisMonth = shipmentRepository.countByCreatedAtBetween(startOfMonth, now);

        // Tỷ lệ theo trạng thái
        long delivered = shipmentRepository.countByCurrentStatusCode("DELIVERED");
        long returned = shipmentRepository.countByCurrentStatusCode("RETURNED");

        double deliveredRate = total > 0 ? Math.round(100.0 * delivered / total * 10.0) / 10.0 : 0;
        double returnedRate = total > 0 ? Math.round(100.0 * returned / total * 10.0) / 10.0 : 0;
        double inProgressRate = Math.max(0, Math.round((100.0 - deliveredRate - returnedRate) * 10.0) / 10.0);

        // COD
        BigDecimal totalCod = shipmentRepository.sumTotalCodAmount();
        BigDecimal pendingCod = shipmentRepository.sumPendingCodAmount();

        // Map theo trạng thái
        List<Object[]> grouped = shipmentRepository.countGroupByStatus();
        Map<String, Long> byStatus = new HashMap<>();
        for (Object[] row : grouped) {
            byStatus.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }

        // Khiếu nại
        long totalComplaints = complaintRepository.count();
        long openComplaints = complaintRepository.countByStatus(ComplaintStatus.OPEN);

        return DashboardStatsResponse.builder()
                .totalShipments(total)
                .shipmentsToday(today)
                .shipmentsThisWeek(thisWeek)
                .shipmentsThisMonth(thisMonth)
                .deliveredRate(deliveredRate)
                .returnedRate(returnedRate)
                .inProgressRate(inProgressRate)
                .avgDeliveryHours(0) // Placeholder — cần thêm query lịch sử nếu cần chính xác
                .totalCodAmount(totalCod)
                .pendingCodAmount(pendingCod)
                .shipmentsByStatus(byStatus)
                .totalComplaints(totalComplaints)
                .openComplaints(openComplaints)
                .generatedAt(now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .build();
    }
}
