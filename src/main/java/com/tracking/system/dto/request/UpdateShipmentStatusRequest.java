package com.tracking.system.dto.request;

import com.tracking.system.enums.UpdateSource;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateShipmentStatusRequest {

    @NotBlank(message = "Tracking code is required")
    private String trackingCode;

    @NotBlank(message = "Status code is required")
    private String statusCode;

    private Long locationPostOfficeId;
    private BigDecimal lat;
    private BigDecimal lng;
    private String note;
    private Long updatedByUserId;
    private UpdateSource updateSource = UpdateSource.APP;
}
