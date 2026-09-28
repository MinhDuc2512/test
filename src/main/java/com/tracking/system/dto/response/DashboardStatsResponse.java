package com.tracking.system.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {

    // Tổng quan đơn hàng
    private long totalShipments;
    private long shipmentsToday;
    private long shipmentsThisWeek;
    private long shipmentsThisMonth;

    // Tỷ lệ
    private double deliveredRate;       // % giao thành công
    private double returnedRate;        // % hoàn hàng
    private double inProgressRate;      // % đang xử lý

    // Thời gian trung bình (giờ)
    private double avgDeliveryHours;

    // COD
    private BigDecimal totalCodAmount;
    private BigDecimal pendingCodAmount;

    // Phân bổ theo trạng thái
    private Map<String, Long> shipmentsByStatus;

    // Khiếu nại
    private long totalComplaints;
    private long openComplaints;

    // Thời gian snapshot
    private String generatedAt;
}
