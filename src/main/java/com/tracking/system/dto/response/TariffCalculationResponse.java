package com.tracking.system.dto.response;

import com.tracking.system.enums.ZoneTier;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffCalculationResponse {

    private String fromProvince;
    private String toProvince;
    private ZoneTier zoneTier;
    private String serviceCode;
    private String serviceName;
    private BigDecimal weightKg;
    private BigDecimal estimatedFee;
    private Integer estimatedSlaHours;
    private String estimatedDeliveryDate;
}
