package com.tracking.system.dto.request;

import com.tracking.system.enums.CodPayer;
import com.tracking.system.enums.ShipmentSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShipmentRequest {

    private Long senderId;

    @NotBlank(message = "Sender name is required")
    private String senderName;

    @NotBlank(message = "Sender phone is required")
    private String senderPhone;

    @NotBlank(message = "Sender address is required")
    private String senderAddress;

    @NotBlank(message = "Sender province is required")
    private String senderProvince;

    @NotBlank(message = "Sender district is required")
    private String senderDistrict;

    private String senderWard;

    @NotBlank(message = "Receiver name is required")
    private String receiverName;

    @NotBlank(message = "Receiver phone is required")
    private String receiverPhone;

    @NotBlank(message = "Receiver address is required")
    private String receiverAddress;

    @NotBlank(message = "Receiver province is required")
    private String receiverProvince;

    @NotBlank(message = "Receiver district is required")
    private String receiverDistrict;

    private String receiverWard;

    private Long serviceTypeId;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private BigDecimal weightKg;

    private BigDecimal lengthCm;
    private BigDecimal widthCm;
    private BigDecimal heightCm;

    private BigDecimal declaredValue;
    private String itemNote;

    private BigDecimal codAmount;
    private CodPayer codPayer = CodPayer.RECEIVER;

    @NotNull(message = "Shipping fee is required")
    private BigDecimal shippingFee;

    private BigDecimal insuranceFee;

    private ShipmentSource source = ShipmentSource.WEB;
    private String partnerOrderId;
}
