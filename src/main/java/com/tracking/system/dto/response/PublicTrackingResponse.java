package com.tracking.system.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicTrackingResponse {

    private String trackingCode;
    private String currentStatusCode;
    private String currentStatusNameVi;
    private String currentStatusNameEn;

    private boolean isVerified; // True if last 4 digits matched

    private String senderName;
    private String senderPhone;
    private String senderProvince;
    private String senderDistrict;
    private String senderAddress;

    private String receiverName;
    private String receiverPhone;
    private String receiverProvince;
    private String receiverDistrict;
    private String receiverAddress;

    private BigDecimal weightKg;
    private BigDecimal codAmount;

    private LocalDateTime createdAt;
    private List<ShipmentStatusHistoryResponse> timeline;
}
