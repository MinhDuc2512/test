package com.tracking.system.dto.request;

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
public class CalculateTariffRequest {

    @NotBlank(message = "From province is required")
    private String fromProvince;

    @NotBlank(message = "To province is required")
    private String toProvince;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private BigDecimal weightKg;

    private String serviceCode = "STD"; // STD, EXPRESS
}
