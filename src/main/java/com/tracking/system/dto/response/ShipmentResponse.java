package com.tracking.system.dto.response;

import com.tracking.system.enums.CodPayer;
import com.tracking.system.enums.ShipmentSource;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentResponse {

    private Long id;
    private String trackingCode;

    private Long senderId;
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private String senderProvince;
    private String senderDistrict;
    private String senderWard;

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String receiverProvince;
    private String receiverDistrict;
    private String receiverWard;

    private Long serviceTypeId;
    private BigDecimal weightKg;
    private BigDecimal lengthCm;
    private BigDecimal widthCm;
    private BigDecimal heightCm;
    private BigDecimal declaredValue;
    private String itemNote;

    private BigDecimal codAmount;
    private CodPayer codPayer;
    private BigDecimal shippingFee;
    private BigDecimal insuranceFee;
    private BigDecimal totalFee;

    private String currentStatusCode;
    private Long currentPostOfficeId;
    private Long originPostOfficeId;
    private Long destinationPostOfficeId;
    private Long assignedShipperId;

    private ShipmentSource source;
    private String partnerOrderId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ShipmentStatusHistoryResponse> history;
}
